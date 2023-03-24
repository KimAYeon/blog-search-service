package com.zetn333.blogsearchservice.api.common.constansts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@AllArgsConstructor
public enum OpenApi {

    KAKAO_BLOG_SEARCH("https://dapi.kakao.com/v2/search/blog",
                "KakaoAK f06a1eec644995b11d618800055c6a82",
                new LinkedMultiValueMap<>(){{
                    set("query", "");        // [String] 검색을 원하는 질의어 특정 블로그 글만 검색하고 싶은 경우, 블로그 url과 검색어를 공백(' ') 구분자로 넣을 수 있음
                    set("sort", "accuracy"); // [String] 결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy
                    set("page", "1");          // [Integer] 결과 페이지 번호, 1~50 사이의 값, 기본 값 1
                    set("size", "10");         // [Integer] 한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10
                  }});

    private final String url;
    private final String key;
    private final MultiValueMap<String, String> params;

    // 카카오 블로그 검색 API 요청 파라미터
    @Getter
    @RequiredArgsConstructor
    public enum kakaoBlogSearchParams {
        SEARCH_KEYWORD("query","검색어", ""),
        PAGE_SORT("sort", "페이지 정렬 방식", "accuracy"),
        PAGE_NUMBER("page","결과 페이지 번호", "1"),
        PAGE_SIZE("size","한 페이지에 보여질 문서 수", "10"),

        SORT_ACCURACY("accuracy","페이지 정렬 방식 - 정확순", "accuracy"),
        SORT_RECENCY("recency","페이지 정렬 방식 - 최신순", "recency"),
        ;

        private final String value;
        private final String description;
        private final String defaultValue;
    }

}