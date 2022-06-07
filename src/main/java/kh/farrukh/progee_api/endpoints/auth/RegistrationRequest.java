package kh.farrukh.progee_api.endpoints.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String name;
    private String username;
    private String email;
    private String password;
}
