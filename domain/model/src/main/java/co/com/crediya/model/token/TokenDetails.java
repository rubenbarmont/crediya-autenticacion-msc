package co.com.crediya.model.token;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDetails {
    private String email;
    private String userId;
    private String role;
}
