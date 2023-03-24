package com.zetn333.blogsearchservice.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "페이징 요청 정보", description = "페이징 요청 정보")
public class PageRequest {

    /* 페이지 번호 */
    @Schema(title = "페이지 번호", description = "요청할 페이지 번호", implementation = Integer.class, example = "1")
    @Min(1) @Max(50)
    private Integer pageNumber;

    /* 페이지 사이즈 */
    @Schema(title = "페이지 사이즈", description = "요청할 페이지 사이즈", implementation = Integer.class, example = "10")
    @Min(1) @Max(50)
    private Integer pageSize;

    /* 페이지 정렬 방식 */
    @Schema(title = "페이지 정렬 방식", description = "요청할 페이지 정렬 방식 - accuracy(정확도순)|recency(최신순)", implementation = String.class,
            allowableValues = {"accuracy", "recency"}, defaultValue = "accuracy", example = "accuracy")
    @Pattern(regexp = "accuracy|recency")
    private String sort;
    
}
