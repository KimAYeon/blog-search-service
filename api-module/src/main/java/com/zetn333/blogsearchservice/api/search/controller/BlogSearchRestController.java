package com.zetn333.blogsearchservice.api.search.controller;

import com.zetn333.blogsearchservice.common.exception.ErrorResponse;
import com.zetn333.blogsearchservice.api.search.dto.SearchBlogRequest;
import com.zetn333.blogsearchservice.api.search.dto.SearchBlogResponse;
import com.zetn333.blogsearchservice.api.search.dto.SelectHotKeywordsRequest;
import com.zetn333.blogsearchservice.api.search.dto.SelectHotKeywordsResponse;
import com.zetn333.blogsearchservice.api.search.service.BlogSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/blog/search")
@Tag(name = "BlogSearchService", description = "블로그 검색 서비스를 관리하는 API")
public class BlogSearchRestController {

    private final BlogSearchService blogSearchService;

    /**
     * 블로그 검색
     */
    @Operation(
            operationId = "searchBlog",
            summary = "블로그 검색",
            description = "검색 및 페이징 조건에 해당하는 블로그 게시글 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SearchBlogResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "실패",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping
    public SearchBlogResponse searchBlog(@Valid @ParameterObject SearchBlogRequest searchBlogRequest) {
        return blogSearchService.searchBlog(searchBlogRequest);
    }


    /**
     * 블로그 검색 인기 키워드 목록 조회
     */
    @Operation(
            operationId = "selectHotKeywords",
            summary = "블로그 검색 인기 키워드 목록 조회",
            description = "조회 조건에 해당하는 최대 10개의 블로그 검색 인기 키워드 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SelectHotKeywordsResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "실패",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/hot-keywords")
    public SelectHotKeywordsResponse selectHotKeywords(@Valid @ParameterObject SelectHotKeywordsRequest selectHotKeywordsRequest) {
        return blogSearchService.selectHotKeywords(selectHotKeywordsRequest);
    }

}
