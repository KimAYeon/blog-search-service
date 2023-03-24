package com.zetn333.blogsearchservice.api.search.dto;

import com.zetn333.blogsearchservice.api.common.dto.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Schema(title = "블로그 검색 요청 정보", description = "블로그 검색 요청 정보")
public class SearchBlogRequest {

    /* 검색어 */
    @Schema(title = "검색어", description = "검색어", implementation = String.class, example = "방탄소년단")
    @NotBlank
    private String searchWord;

    /* 페이징 요청 정보 */
    @Valid
    private PageRequest page = new PageRequest();

}
