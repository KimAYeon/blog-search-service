package com.zetn333.blogsearchservice.api.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "블로그 게시글 검색 결과 정보", description = "검색된 블로그 게시글 정보")
public class SearchBlogPostResponse {

    /* 블로그 글 제목 */
    @Schema(title = "블로그 글 제목", description = "블로그 글 제목", implementation = String.class, example = "작은 <b>집</b> <b>짓기</b> 기본컨셉 - <b>집</b><b>짓기</b> 초기구상하기")
    @NotBlank
    private String postTitle;

    /* 블로그 글 요약 */
    @Schema(title = "블로그 글 요약", description = "블로그 글 요약", implementation = String.class,
            example = "이 점은 <b>집</b>을 지으면서 고민해보아야 한다. 하지만, 금액에 대한 가성비 대비 크게 문제되지 않을 부분이라 생각하여 설계로 극복하자고 생각했다. " +
                    "전체 <b>집</b><b>짓기</b>의 기본방향은 크게 세 가지이다. 우선은 여가의 영역 증대이다. 현대 시대 일도 중요하지만, 여가시간 <b>집</b>에서 어떻게 보내느냐가 중요하니깐 이를 기본적...")
    @NotBlank
    private String postContentsSummary;

    /* 블로그 글 URL */
    @Schema(title = "블로그 글 URL", description = "블로그 글 URL", implementation = String.class, example = "https://brunch.co.kr/@tourism/91")
    @NotBlank
    private String postUrl;

    /* 블로그 명 */
    @Schema(title = "블로그 명", description = "블로그 명", implementation = String.class, example = "정란수의 브런치")
    @NotBlank
    private String blogName;

    /* 대표 미리보기 이미지 URL */
    @Schema(title = "대표 미리보기 이미지 URL", description = "검색 시스템에서 추출한 대표 미리보기 이미지 URL, 미리보기 크기 및 화질은 변경될 수 있음",
            implementation = String.class, example = "http://search3.kakaocdn.net/argon/130x130_85_c/7r6ygzbvBDc")
    @NotBlank
    private String thumbnailUrl;

    /* 블로그 글 작성 일시 */
    @Schema(title = "블로그 글 작성 일시", description = "블로그 글 작성 일시 (ISO 8601 [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz])",
            implementation = Date.class, example = "2017-05-07T18:50:07.000+09:00")
    private Date writeDateTime;

}
