package org.jj.jjblog.error.handler;

import lombok.extern.log4j.Log4j2;
import org.hibernate.PropertyValueException;
import org.jj.jjblog.error.CustomException;
import org.jj.jjblog.error.ErrorCode;
import org.jj.jjblog.error.ErrorResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseEntity> handlerCustomException(CustomException e) {
        log.info(e.getMessage());
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }
    @ExceptionHandler(PropertyValueException.class)
    protected ResponseEntity<ErrorResponseEntity> handlerPropertyValueException() {
        return ErrorResponseEntity.toResponseEntity(ErrorCode.FAIL_REGISTER);
    }
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponseEntity> handleInternalAuthenticationServiceException() {
        return ErrorResponseEntity.toResponseEntity(ErrorCode.USER_NOT_FOUND);
    }
}
