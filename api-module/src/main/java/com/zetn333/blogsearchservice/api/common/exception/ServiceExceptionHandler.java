package com.zetn333.blogsearchservice.api.common.exception;

import com.zetn333.blogsearchservice.api.common.constansts.ErrorCode;
import com.zetn333.blogsearchservice.api.common.dto.ErrorResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@NoArgsConstructor
@RestControllerAdvice
public class ServiceExceptionHandler {

    /**
     * Error Response 형태로 Response Entity 생성
     * @param customServiceException
     * @return ResponseEntity<ErrorResponse>
     */
    private ResponseEntity<ErrorResponse> makeResponseEntity(CustomServiceException customServiceException) {
        final String errorMessage = customServiceException.getMessage();

        if(log.isInfoEnabled())
            log.info("{}|{}|{}", customServiceException.getStatus(), customServiceException.getCode(), errorMessage);

        final ErrorResponse response = ErrorResponse.of(customServiceException.getCode(), errorMessage);
        return new ResponseEntity<>(response, HttpStatus.valueOf(customServiceException.getStatus()));
    }

    /**
     * API Service에서 발생하는 Service Exception 결과 처리
     *
     * @param validException
     * @return
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleValidException(BindException validException) {
        log.debug("===== Validation Error : {}", validException.getBindingResult().getAllErrors());

        // Default ErrorCode
        CustomServiceException customServiceException = CustomServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER);
        try {
            // Validation 에 대한 Annotation Code
            ObjectError objectError = validException.getAllErrors().get(0);
            String errorCode = objectError.getCode();
            switch (errorCode) {
                case "Size":
                    Object[] sizeArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage(), // Field name
                            objectError.getArguments()[2], // Max
                            objectError.getArguments()[1]}; // Min

                    customServiceException = CustomServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_SIZE, sizeArgs);
                    break;

                case "Max":
                    Object[] maxArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage(),
                            objectError.getArguments()[1]}; // Max
                    customServiceException = CustomServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_MAX, maxArgs);
                    break;
                case "Min":
                    Object[] minArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage(),
                            objectError.getArguments()[1]}; // Min
                    customServiceException = CustomServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_MIN, minArgs);
                    break;
                case "NotNull":
                case "NotBlank":
                case "NotEmpty":
                    Object[] notEmptyArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage()};

                    customServiceException = CustomServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_NOTEMPTY, notEmptyArgs);
                    break;
                case "Pattern":
                    Object[] patternArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage(),
                            objectError.getArguments()[1]}; // Min
                    customServiceException = CustomServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_FORMAT, patternArgs);
                    break;
                default:
                    Object[] defaultArgs = {((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage()};
                    customServiceException = CustomServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER, defaultArgs);
                    break;
            }
        } catch(Exception e) {
            log.error("INVALID_INPUT_PARAMETER(validException)", validException);
            log.error("INVALID_INPUT_PARAMETER(Exception)", e);
        }

        return makeResponseEntity(customServiceException);
    }

    /**
     * API Service에서 발생하는 Service Exception 결과 처리
     *
     * @param customServiceException
     * @return
     */
    @ExceptionHandler(CustomServiceException.class)
    protected ResponseEntity<ErrorResponse> handleServiceException(CustomServiceException customServiceException) {
        return makeResponseEntity(customServiceException);
    }

    /**
     * 개발자가 인지하지 못한 에러가 발생했을 경우에 대한 처리
     * 500 ERROR
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("INTERNAL_SERVER_ERROR", e);

        String errorMessage;
        try {
            errorMessage = ErrorCode.INTERNAL_SERVER_ERROR.getMessage();
        }catch(Exception e2) {
            errorMessage = e2.getMessage();
        }

        // 로깅
        if(log.isInfoEnabled())
            log.info("{}|{}|{}",ErrorCode.INTERNAL_SERVER_ERROR.getStatus(), ErrorCode.INTERNAL_SERVER_ERROR.getCode(), errorMessage);

        final ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
