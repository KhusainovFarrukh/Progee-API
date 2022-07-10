package kh.farrukh.progee_api.endpoints.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"access_token", "access_token_expires", "refresh_token", "refresh_token_expires", "role"})
public class AuthResponse {

    private String role;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("access_token_expires")
    private String accessTokenExpires;
    @JsonProperty("refresh_token_expires")
    private String refreshTokenExpires;

    public AuthResponse(
            Object role,
            Object accessToken,
            Object refreshToken,
            Object accessTokenExpires,
            Object refreshTokenExpires
    ) {
        this.role = (String) role;
        this.accessToken = (String) accessToken;
        this.refreshToken = (String) refreshToken;
        this.accessTokenExpires = (String) accessTokenExpires;
        this.refreshTokenExpires = (String) refreshTokenExpires;
    }
}
