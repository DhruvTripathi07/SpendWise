package com.spendwise.backend.exception;

import com.spendwise.backend.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleExpenseNotFound(
            ExpenseNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("message", ex.getMessage());
        response.put("status", 404);

        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();

        response.put("status", 400);
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return response;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleResourceNotFound(
            ResourceNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("message", ex.getMessage());
        response.put("status", 404);

        return response;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Invalid value for parameter: " + ex.getName());
        response.put("status", 400);

        return response;
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("message", ex.getMessage());
        response.put("status", 409);

        return response;
    }
}