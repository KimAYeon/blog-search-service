package com.zetn333.blogsearchservice.api.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@Schema(title = "인기 검색 키워드 목록 조회 요청 정보", description = "인기 검색 키워드 목록 조회 요청 정보")
public class SelectHotKeywordsRequest {

//    /* 검색어 */
//    @Schema(title = "검색어", description = "검색어가 포함된 키워드로 조회", implementation = String.class, example = "방탄소년단")
//    private String searchWord;
//
//    /* 키워드 조회 개수 */
//    @Schema(title = "키워드 조회 개수", description = "요청할 키워드 조회 개수 (1~10)", implementation = Integer.class, example = "10",
//            defaultValue = "10")
//    @Min(1) @Max(10)
//    private Integer keywordCount = 10;

}
