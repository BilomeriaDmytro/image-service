package com.test.imageservice.presentation.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ExceptionResponseEntity exceptionResponseEntity) {
        return new ResponseEntity<>(exceptionResponseEntity, exceptionResponseEntity.getStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex){

        ExceptionResponseEntity exceptionResponseEntity = new ExceptionResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(),ex);
        return buildResponseEntity(exceptionResponseEntity);
    }

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<Object> handleForbiddenException(ForbiddenException ex){

        ExceptionResponseEntity exceptionResponseEntity = new ExceptionResponseEntity(HttpStatus.FORBIDDEN, ex.getMessage(),ex);
        return buildResponseEntity(exceptionResponseEntity);
    }

    @ExceptionHandler(InputException.class)
    protected ResponseEntity<Object> handleInputException(InputException ex){

        ExceptionResponseEntity exceptionResponseEntity = new ExceptionResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(),ex);
        return buildResponseEntity(exceptionResponseEntity);
    }
}