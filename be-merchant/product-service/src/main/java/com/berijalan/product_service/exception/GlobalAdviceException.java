package com.berijalan.product_service.exception;


import com.berijalan.product_service.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

@RestControllerAdvice
public class GlobalAdviceException {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<?>>handleBadRequest(BadRequestException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.failed(exception.getMessage()));
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<BaseResponse<?>>handleDataNotFound(DataNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.failed(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>>handleMethodArgumentNotValid(MethodArgumentNotValidException exception){
        ArrayList<String> errorMessage = new ArrayList<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errorMessage.add(error.getDefaultMessage())
                );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.failed("Data tidak valid", errorMessage));
    }
}
