package com.zetn333.blogsearchservice.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title = "페이징 결과 정보", description = "페이징 결과 정보, 사용된 API에 따라 필드 사용이 상이함.")
public class PageResponse {

    /* 페이지 번호 */
    @Schema(title = "페이지 번호", description = "조회된 페이지 번호", implementation = Integer.class)
    private Integer pageNumber;

    /* 페이지 사이즈 */
    @Schema(title = "페이지 사이즈", description = "조회된 페이지 사이즈", implementation = Integer.class)
    private Integer pageSize;

    /* 전체 게시글 수 */
    @Schema(title = "전체 게시글 수", description = "조회된 전체 게시글 수", implementation = Integer.class)
    private Integer totalCount;

    /* 전체 페이지 수 */
    @Schema(title = "전체 페이지 수", description = "조회된 전체 페이지 수", implementation = Integer.class)
    private Integer totalPageCount;

    /* 노출 가능 게시글 수 */
    @Schema(title = "노출 가능 게시글 수", description = "전체 게시글 중에 노출 가능한 게시글 수", implementation = Integer.class)
    private Integer pageableCount;

    /* 마지막 페이지 여부 */
    @Schema(title = "마지막 페이지 여부", description = "현재 페이지가 마지막 페이지인지 여부", implementation = Boolean.class)
    private Boolean isEnd;

}
