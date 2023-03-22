package com.zetn333.blogsearchservice.common.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
@Getter @Setter @ToString
public class ServiceException extends RuntimeException {

    private static ApplicationContext context;
    private final int status;
    private final String code;
    private final String message;
    private final Object[] args;

    public ServiceException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public ServiceException(ErrorCode errorCode, Object[] args) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.args = args;
        this.message = errorCode.getMessage();
    }

    public static ServiceException of(ErrorCode errorCode) {
        return new ServiceException(errorCode);
    }

    public static ServiceException of(ErrorCode errorCode, Object[] args) {
        return new ServiceException(errorCode, args);
    }

}
