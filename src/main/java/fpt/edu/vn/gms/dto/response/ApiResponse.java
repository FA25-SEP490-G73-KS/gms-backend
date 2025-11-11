package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Timestamp when the response was created", example = "1734567890123")
    Long timestamp;

    @Schema(description = "HTTP status code", example = "200")
    Integer statusCode;

    @Schema(description = "Description or message for the response", example = "Request successful")
    String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "The actual response data")
    T result;

    // Static factory methods for convenience
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(System.currentTimeMillis())
                .statusCode(200)
                .message(message)
                .result(data)
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(Instant.now().toEpochMilli())
                .statusCode(201)
                .message(message)
                .result(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return ApiResponse.<T>builder()
                .timestamp(System.currentTimeMillis())
                .statusCode(statusCode)
                .message(message)
                .build();
    }
}
