package com.zetn333.blogsearchservice.api.common.constansts;

import lombok.AllArgsConstructor;
import lombok.Getter;

//-----------------------------------------------------------------------------------
//Error Code(9자리) : {서비스코드:3}{위험도Level:1}{N:1}{코드특성:1}{ErrorCode:3}
//-----------------------------------------------------------------------------------
// 서비스코드        : MNU(메뉴),ROL(인가),API(API),PGE(페이지),ATH(인증),COD(코드),
//                   CMN(공통), ex)MNUN4001
// 위험도(Level)    : L.1~3 (업무에서 판단), L.4(Interface 오류) , L5.(System 오류)
// N               : Result Code 시작을 나타내는 고정 문자
// 코드 특성 분류    : 1 - Parameter 오류 / 2 - Header 오류 / 3 - Data 오류
//                   4 ~ 7 - 파트에서 정의하여 사용, 8 - Interface 오류, 9 - System 오류
//-----------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /** #################################################################################
     * Backend API 에러 코드 정의
     ################################################################################## */

    // WebClient
    INVALID_INPUT_OPEN_API(400, "WCT1N1001", "Open API 요청 오류 입니다."),
    SERVER_ERROR_OPEN_API(500, "WCT1N9999", "Open API 시스템 오류 입니다."),

    /** #################################################################################
     * 공통 에러 코드 정의
     ################################################################################## */

    // Input 오류
    INVALID_INPUT_PARAMETER(400, "CMN1N1000", "{0} 입력 값을 잘못 입력하였습니다."),
    INVALID_INPUT_PARAMETER_NOTEMPTY(400, "CMN1N1001", "{0} 입력 값은 필수 항목 입니다."),
    INVALID_INPUT_PARAMETER_MAX(400, "CMN1N1002", "{0} 입력 값은 {1} 자리 이하로 입력해야 합니다."),
    INVALID_INPUT_PARAMETER_MIN(400, "CMN1N1003", "{0} 입력 값은 {1} 자리 이상으로 입력해야 합니다."),
    INVALID_INPUT_PARAMETER_SIZE(400, "CMN1N1004", "{0} 입력 값은 {1} ~ {2} 자리로 입력해야 합니다."),
    INVALID_INPUT_PARAMETER_DUPLICATE(400, "CMN1N1005", "{0} 중복된 값이 존재합니다."), // 추가
    INVALID_INPUT_PARAMETER_FORMAT(400, "CMN1N1006", "{0} 데이터 포맷 또는 유효성 검증 오류입니다."),
    INVALID_INPUT_PARAMETER_START_DATE(400, "CMN1N1007", "{0} 시작일은 종료일과 같거나 이전이어야 합니다."),
    INVALID_INPUT_PARAMETER_CATEGORY(400, "CMN1N1008", "{0} 입력 값은 {1} ~ {2} 자리로 입력해야 합니다."),
    // DATA 오류
    DATA_NOT_FOUND(404, "CMN1N2009", "조회한 정보가 없습니다."),
    // 시스템 오류
    INTERNAL_SERVER_ERROR(500, "CMN1N9999", "알수 없는 오류가 발생했습니다. 관리자에게 문의 바랍니다.");

    private final int status;
    private final String code;
    private final String message;

}
