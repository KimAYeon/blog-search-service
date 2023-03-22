package com.zetn333.blogsearchservice.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "에러 결과 정보")
public class ErrorResponse {

    /* 에러 코드 */
    @Schema(title = "에러 코드", description = "정의된 서비스 에러 코드", implementation = String.class, example = "CMN1N2001")
    private String errorCode;

    /* 에러 메세지 */
    @Schema(title = "에러 메시지", description = "정의된 서비스 에러 메시지", implementation = String.class, example = "데이터를 찾을 수 없습니다.")
    private String errorMessage;

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }

}
