package fpt.edu.vn.gms.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpException(ResponseStatusException e) {
        if (e.getStatusCode().value() >= 500) {
            log.error("Unknown error: {}", e);
        }

        return ResponseEntity.status(e.getStatusCode())
                .body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
