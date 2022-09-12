package kh.farrukh.progee_api.auth.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Login request user to deserialize Spring Security login endpoint request body
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    private String email;
    private String password;
}
