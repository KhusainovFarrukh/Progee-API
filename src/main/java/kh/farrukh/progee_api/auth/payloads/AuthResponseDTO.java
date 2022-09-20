package kh.farrukh.progee_api.auth.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"access_token", "access_token_expires", "refresh_token", "refresh_token_expires", "role"})
public class AuthResponseDTO {

    private RoleResponseDTO role;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("access_token_expires")
    private ZonedDateTime accessTokenExpires;
    @JsonProperty("refresh_token_expires")
    private ZonedDateTime refreshTokenExpires;
}
