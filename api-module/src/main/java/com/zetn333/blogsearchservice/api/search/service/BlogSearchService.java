package com.zetn333.blogsearchservice.api.search.service;

import com.zetn333.blogsearchservice.api.common.constansts.ErrorCode;
import com.zetn333.blogsearchservice.api.common.constansts.OpenApi;
import com.zetn333.blogsearchservice.api.common.dto.PageResponse;
import com.zetn333.blogsearchservice.api.common.exception.CustomServiceException;
import com.zetn333.blogsearchservice.api.search.dto.*;
import com.zetn333.blogsearchservice.common.entity.SearchStatusEntity;
import com.zetn333.blogsearchservice.common.repository.SearchStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogSearchService {

    private final ModelMapper modelMapper;
    private final WebClient webClient;

    private final SearchStatusRepository searchStatusRepository;

    /**
     * 블로그 검색
     * @param  searchBlogRequest 검색 조건
     * @return SearchBlogResponse 블로그 검색 결과 정보
     */
    @Transactional
    public SearchBlogResponse searchBlog(SearchBlogRequest searchBlogRequest) {
        SearchBlogResponse searchBlogResponse;

        try {
            // 카카오 API
            searchBlogResponse = requestKakaoApi(searchBlogRequest);
        } catch (WebClientException e) {
            throw CustomServiceException.of(ErrorCode.SERVER_ERROR_OPEN_API);
            // TODO: 네이버 API
        }

        // 검색 현황 테이블 업데이트
        updateSearchKeywordStatus(searchBlogRequest);

        return searchBlogResponse;
    }

    /**
     * 카카오 블로그 검색 API 호출
     * @param searchBlogRequest
     * @return SearchBlogResponse 검색된 블로그 정보
     */
    private SearchBlogResponse requestKakaoApi(SearchBlogRequest searchBlogRequest) {

        // WebClient를 이용하여 카카오 API 요청 스펙에 맞춰 REST API 호출 - OpenApi Class 정의 참조
        // https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog

        // 필수값이 아닌 요청 파라미터 초기 세팅
        if (ObjectUtils.isEmpty(searchBlogRequest.getPage().getPageNumber())) {
            searchBlogRequest.getPage().setPageNumber(Integer.valueOf(OpenApi.kakaoBlogSearchParams.PAGE_NUMBER.getDefaultValue()));
        }
        if (ObjectUtils.isEmpty(searchBlogRequest.getPage().getPageSize())) {
            searchBlogRequest.getPage().setPageSize(Integer.valueOf(OpenApi.kakaoBlogSearchParams.PAGE_SIZE.getDefaultValue()));
        }
        if (ObjectUtils.isEmpty(searchBlogRequest.getPage().getSort())) {
            searchBlogRequest.getPage().setSort(OpenApi.kakaoBlogSearchParams.PAGE_SORT.getDefaultValue());
        }

        KakaoBlogSearchResponseDto kakaoBlogSearchResponseDto = webClient.get()
                .uri(OpenApi.KAKAO_BLOG_SEARCH.getUrl(),
                        uriBuilder -> uriBuilder
                                .queryParam(OpenApi.kakaoBlogSearchParams.SEARCH_KEYWORD.getValue(), searchBlogRequest.getSearchWord())
                                .queryParam(OpenApi.kakaoBlogSearchParams.PAGE_NUMBER.getValue(), searchBlogRequest.getPage().getPageNumber())
                                .queryParam(OpenApi.kakaoBlogSearchParams.PAGE_SIZE.getValue(), searchBlogRequest.getPage().getPageSize())
                                .queryParam(OpenApi.kakaoBlogSearchParams.PAGE_SORT.getValue(), searchBlogRequest.getPage().getSort())
                                .build())
                .header(HttpHeaders.AUTHORIZATION, OpenApi.KAKAO_BLOG_SEARCH.getKey())
                .retrieve()
                .bodyToMono(KakaoBlogSearchResponseDto.class)
                .block();

        List<SearchBlogPostResponse> searchBlogPostResponses = new ArrayList<>();

        // 카카오 API 응답 값을 서비스 API 응답 스펙에 맞춰 매핑
        // KakaoBlogSearchResponseDto -> PageResponse 매핑
        PageResponse pageResponse = PageResponse.builder()
                .pageNumber(searchBlogRequest.getPage().getPageNumber())
                .pageSize(searchBlogRequest.getPage().getPageSize())
                .totalCount(kakaoBlogSearchResponseDto.getMeta().getTotal_count())
                .pageableCount(kakaoBlogSearchResponseDto.getMeta().getPageable_count())
                .isEnd(kakaoBlogSearchResponseDto.getMeta().getIs_end())
                .build();

        // KakaoBlogSearchResponseDto -> SearchBlogPostResponse 매핑
        kakaoBlogSearchResponseDto.getDocuments().forEach(document -> {
            SearchBlogPostResponse searchBlogPostResponse = SearchBlogPostResponse.builder()
                    .postTitle(document.getTitle())
                    .postContentsSummary(document.getContents())
                    .postUrl(document.getUrl())
                    .blogName(document.getBlogname())
                    .thumbnailUrl(document.getThumbnail())
                    .writeDateTime(document.getDatetime())
                    .build();

            searchBlogPostResponses.add(searchBlogPostResponse);
        });

        log.info("kakao api response = {}", SearchBlogResponse.of(pageResponse, searchBlogPostResponses));

        return SearchBlogResponse.of(pageResponse, searchBlogPostResponses);
    }

    /**
     * 검색 키워드 현황 업데이트
     * 검색 시간 절감을 위해 비동기 처리
     * @param searchBlogRequest
     * @return SearchStatusEntity 업데이트 된 엔티티
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SearchStatusEntity updateSearchKeywordStatus(SearchBlogRequest searchBlogRequest) {
        // 기존 검색 키워드 조회
        SearchStatusEntity searchStatusEntity = searchStatusRepository.findBySearchKeyword(searchBlogRequest.getSearchWord());

        log.info("searchStatusEntity before = {}", searchStatusEntity);

        if (ObjectUtils.isEmpty(searchStatusEntity)) {
            // 기존 검색 키워드가 존재하지 않는 경우, 새 엔티티 생성
            searchStatusEntity = SearchStatusEntity.builder()
                    .searchKeyword(searchBlogRequest.getSearchWord())
                    .searchCount(BigInteger.ONE)
                    .lastSearchDateTime(LocalDateTime.now())
                    .build();
        } else {
            // 기존 검색 키워드가 존재하는 경우, 엔티티 검색 횟수 증가 및 최근 검색 일시 업데이트
            searchStatusEntity.plusSearchCount();
            searchStatusEntity.updateLastSearchDateTime();
        }

        log.info("searchStatusEntity after = {}", searchStatusEntity);

        // 엔티티 등록 및 수정
        searchStatusEntity = searchStatusRepository.save(searchStatusEntity);

        return searchStatusEntity;
    }

    /**
     * 인기 검색 키워드 목록 조회
     * @param selectHotKeywordsRequest
     * @return SelectHotKeywordsResponse 인기 검색 키워드 조회 결과 정보
     */
    public SelectHotKeywordsResponse selectHotKeywords(SelectHotKeywordsRequest selectHotKeywordsRequest) {

        // 사용자들이 많이 검색한 순서대로, 최대 10개의 검색 키워드를 제공
        List<SearchStatusEntity> searchStatusEntities = searchStatusRepository.findTop10BySearchCountDesc();

        log.info("hotKeywords = {}", searchStatusEntities);

        return SelectHotKeywordsResponse.of(modelMapper.map(
                searchStatusEntities, new TypeToken<List<SelectHotKeywordResponse>>() {}.getType()));
    }

}
