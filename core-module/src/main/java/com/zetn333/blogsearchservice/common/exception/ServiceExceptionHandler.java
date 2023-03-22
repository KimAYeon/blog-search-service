package com.zetn333.blogsearchservice.common.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.Validation;
import javax.validation.Validator;

@Slf4j
@RestControllerAdvice
@NoArgsConstructor
@AllArgsConstructor
public class ServiceExceptionHandler {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private ResponseEntity<ErrorResponse> makeResponseEntity(ServiceException serviceException) {

        final String errorMessage = serviceException.getMessage();

        // 로깅
        if(log.isInfoEnabled())
            log.info("{}|{}|{}",serviceException.getStatus(), serviceException.getCode(), errorMessage);

        final ErrorResponse response = ErrorResponse.of(serviceException.getCode(), errorMessage);
        return new ResponseEntity<>(response,HttpStatus.valueOf(serviceException.getStatus()));
    }

    private String getMessage(ErrorCode errorCode) {
        return errorCode.getMessage();
    }

    /**
     * API Service에서 발생하는 Service Exception 결과 처리
     *
     * @param validException
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidException( MethodArgumentNotValidException validException) {
        log.debug("===== Validation Error : {}", validException.getBindingResult().getAllErrors());

        // Default ErrorCode
        ServiceException serviceException = ServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER);
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

                    serviceException = ServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_SIZE, sizeArgs);
                    break;

                case "Max":
                    Object[] maxArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage(),
                            objectError.getArguments()[1]}; // Max
                    serviceException = ServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_MAX, maxArgs);
                    break;
                case "Min":
                    Object[] minArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage(),
                            objectError.getArguments()[1]}; // Min
                    serviceException = ServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_MIN, minArgs);
                    break;
                case "NotNull":
                case "NotBlank":
                case "NotEmpty":
                    Object[] notEmptyArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage()};

                    serviceException = ServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_NOTEMPTY, notEmptyArgs);
                    break;
                case "Pattern":
                    Object[] patternArgs = {
                            ((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage(),
                            objectError.getArguments()[1]}; // Min
                    serviceException = ServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER_FORMAT, patternArgs);
                    break;
                default:
                    Object[] defaultArgs = {((DefaultMessageSourceResolvable)objectError.getArguments()[0]).getDefaultMessage()};
                    serviceException = ServiceException.of(ErrorCode.INVALID_INPUT_PARAMETER, defaultArgs);
                    break;
            }
        }catch(Exception e) {
            log.error("INVALID_INPUT_PARAMETER(validException)", validException);
            log.error("INVALID_INPUT_PARAMETER(Exception)", e);
        }

        return makeResponseEntity(serviceException);
    }

    /**
     * API Service에서 발생하는 Service Exception 결과 처리
     *
     * @param serviceException
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    protected ResponseEntity<ErrorResponse> handleServiceException( ServiceException serviceException) {
        return makeResponseEntity(serviceException);
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
            errorMessage = this.getMessage(ErrorCode.INTERNAL_SERVER_ERROR);
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
