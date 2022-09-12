package kh.farrukh.progee_api.auth.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * It's a POJO that represents a registration request.
 * Contains information about user to be registered
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;
    @JsonProperty("username")
    @NotBlank(message = "Username must not be blank")
    @Size(max = 16, message = "Username must be shorter than 16 characters")
    private String uniqueUsername;
    @NotBlank(message = "Email must not be blank")
    private String email;
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password length must be at least 8 characters")
    private String password;
    @JsonProperty("image_id")
    private long imageId;
}
