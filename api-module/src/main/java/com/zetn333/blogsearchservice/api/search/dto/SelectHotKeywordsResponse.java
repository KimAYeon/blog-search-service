package com.zetn333.blogsearchservice.api.search.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder({"list"})
@Schema(description = "인기 검색 키워드 목록 조회 결과 정보")
public class SelectHotKeywordsResponse {

    @ArraySchema(schema = @Schema(implementation = SelectHotKeywordResponse.class))
    @NotNull
    private List<SelectHotKeywordResponse> list = new ArrayList<>();

    private SelectHotKeywordsResponse(final List<SelectHotKeywordResponse> list) {
        this.list = list;
    }

    public static SelectHotKeywordsResponse of(final List<SelectHotKeywordResponse> list) {
        return new SelectHotKeywordsResponse(list);
    }

}
