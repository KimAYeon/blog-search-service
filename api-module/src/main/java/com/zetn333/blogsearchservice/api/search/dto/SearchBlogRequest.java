package com.zetn333.blogsearchservice.api.search.dto;

import com.zetn333.blogsearchservice.api.common.dto.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(title = "블로그 검색 요청 정보", description = "블로그 검색 요청 정보")
public class SearchBlogRequest {

    /* 검색어 */
    @Schema(title = "검색어", description = "검색어", implementation = String.class, example = "방탄소년단")
    private String searchWord;

    private PageRequest page;

}
