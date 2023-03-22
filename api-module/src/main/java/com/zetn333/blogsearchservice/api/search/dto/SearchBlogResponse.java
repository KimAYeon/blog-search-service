package com.zetn333.blogsearchservice.api.search.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.zetn333.blogsearchservice.api.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"page", "list"})
@Schema(description = "블로그 검색 결과 정보")
public class SearchBlogResponse {

    @Schema(description = "검색 페이징 결과 정보")
    private PageResponse page;

    @ArraySchema(schema = @Schema(implementation = SearchBlogPostResponse.class))
    @NotNull
    private List<SearchBlogPostResponse> list = new ArrayList<>();

    private SearchBlogResponse(final PageResponse page, final List<SearchBlogPostResponse> list) {
        this.page = page;
        this.list = list;
    }

    public static SearchBlogResponse of(final PageResponse page, final List<SearchBlogPostResponse> list) {
        return new SearchBlogResponse(page, list);
    }

}
