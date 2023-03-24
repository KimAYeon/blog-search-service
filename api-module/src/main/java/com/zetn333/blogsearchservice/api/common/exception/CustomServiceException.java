package com.zetn333.blogsearchservice.api.common.exception;

import com.zetn333.blogsearchservice.api.common.constansts.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
@Getter @Setter @ToString
public class CustomServiceException extends RuntimeException {

    private static ApplicationContext context;
    private final int status;
    private final String code;
    private final String message;
    private final Object[] args;

    public CustomServiceException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public CustomServiceException(ErrorCode errorCode, Object[] args) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.args = args;
        this.message = errorCode.getMessage();
    }

    public static CustomServiceException of(ErrorCode errorCode) {
        return new CustomServiceException(errorCode);
    }

    public static CustomServiceException of(ErrorCode errorCode, Object[] args) {
        return new CustomServiceException(errorCode, args);
    }

}
