package com.zetn333.blogsearchservice.api.search.service;

import com.zetn333.blogsearchservice.api.common.dto.PageResponse;
import com.zetn333.blogsearchservice.api.search.dto.*;
import com.zetn333.blogsearchservice.common.entity.SearchStatusEntity;
import com.zetn333.blogsearchservice.common.exception.ServiceException;
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
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogSearchService {

    private final WebClient webClient;
    private final ModelMapper modelMapper;
    private final SearchStatusRepository searchStatusRepository;

    /**
     * 블로그 검색
     * @param  searchBlogRequest 검색 조건
     * @return SearchBlogResponse 블로그 검색 결과 정보
     */
    @Transactional
    public SearchBlogResponse searchBlog(SearchBlogRequest searchBlogRequest) {
        SearchBlogResponse searchBlogResponse = new SearchBlogResponse();

        // 카카오 API
        searchBlogResponse = requestKakaoApi(searchBlogRequest);

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
        KakaoSearchBlogDto kakaoSearchBlogDto = webClient.get()
                                        .uri("https://dapi.kakao.com/v2/search/blog",
                                                uriBuilder -> uriBuilder
                                                        .queryParam("query", searchBlogRequest.getSearchWord())
                                                        .queryParam("page", searchBlogRequest.getPage().getPageNumber())
                                                        .queryParam("size", searchBlogRequest.getPage().getPageSize())
                                                        .queryParam("sort", searchBlogRequest.getPage().getSort())
                                                        .build())
                                        .header(HttpHeaders.AUTHORIZATION, "KakaoAK f06a1eec644995b11d618800055c6a82")
                                        .retrieve()
                                        .bodyToMono(KakaoSearchBlogDto.class)
                                        .block();

        List<SearchBlogPostResponse> searchBlogPostResponses = new ArrayList<>();

        PageResponse pageResponse = PageResponse.builder()
                .pageNumber(searchBlogRequest.getPage().getPageNumber())
                .pageSize(searchBlogRequest.getPage().getPageSize())
                .totalCount(kakaoSearchBlogDto.getMeta().getTotal_count())
                .pageableCount(kakaoSearchBlogDto.getMeta().getPageable_count())
                .isEnd(kakaoSearchBlogDto.getMeta().getIs_end())
                .build();

        kakaoSearchBlogDto.getDocuments().forEach(document -> {
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
     * @param searchBlogRequest
     * @return SearchStatusEntity 업데이트 된 엔티티
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SearchStatusEntity updateSearchKeywordStatus(SearchBlogRequest searchBlogRequest) {
        // 기존 검색 키워드 조회
        SearchStatusEntity searchStatusEntity = searchStatusRepository.findBySearchKeyword(searchBlogRequest.getSearchWord());

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

        // 사용자들이 많이 검색한 순서대로, 최대 10개의 검색 키워드를 제공합니다.
        List<SearchStatusEntity> searchStatusEntities = searchStatusRepository.findTop10BySearchCountDesc();

        // 검색어 별로 검색된 횟수도 함께 표기해 주세요.
        return SelectHotKeywordsResponse.of(modelMapper.map(
                searchStatusEntities, new TypeToken<List<SelectHotKeywordResponse>>() {}.getType()));
    }

}
