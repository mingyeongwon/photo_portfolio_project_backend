package com.example.portfolio.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
	
	// 커스텀 예외를 모두 여기서 처리
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDto> handleCustomException(CustomException ex) {
        return ErrorDto.toResponseEntity(ex);
    }
    
    // DB 관련 예외는 여기서 모두 처리
    @ExceptionHandler(DataAccessException.class) 
    protected ResponseEntity<ErrorDto> handleDataAccessException(DataAccessException ex) {
        CustomException customException = new CustomException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.DATABASE_ERROR,
            "Database error occurred: " + ex.getMessage()
        );
        return ErrorDto.toResponseEntity(customException);
    }
    
    // 처리되지 않은 모든 예외를 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorDto> handleAllException(Exception ex) {
        CustomException customException = new CustomException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.UNKNOWN,
            "Unexpected error occurred: " + ex.getMessage()
        );
        return ErrorDto.toResponseEntity(customException);
    }
}