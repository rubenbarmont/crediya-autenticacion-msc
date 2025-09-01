package co.com.crediya.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private String path;
}
