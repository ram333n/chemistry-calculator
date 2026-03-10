package org.prokopchuk.chemistry_calculator.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFormulaException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFormula(
            InvalidFormulaException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(errorBody(400, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(InvalidEquationException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEquation(
            InvalidEquationException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(errorBody(400, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(errorBody(400, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(500, ex.getMessage(), request.getRequestURI()));
    }

    private Map<String, Object> errorBody(int status, String message, String path) {
        return Map.of("status", status, "message", message, "path", path);
    }
}
