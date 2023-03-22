package com.zetn333.blogsearchservice.api.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "인기 검색 키워드 조회 결과 정보", description = "조회된 인기 검색 키워드 결과 정보")
public class SelectHotKeywordResponse {

    /* 검색 키워드 */
    @Schema(title = "검색 키워드", description = "검색 키워드", implementation = String.class, example = "방탄소년단")
    private String searchKeyword;

    /* 검색 횟수 */
    @Schema(title = "검색 횟수", description = "검색 횟수", implementation = BigInteger.class, example = "2023")
    private BigInteger searchCount;

    /* 최근 검색 일시 */
    @Schema(title = "최근 검색 일시", description = "최근 검색 일시", implementation = LocalDateTime.class, example = "2017-05-07T18:50:07")
    private LocalDateTime lastSearchDateTime;

}
