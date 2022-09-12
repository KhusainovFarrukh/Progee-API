package kh.farrukh.progee_api.user.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetUserPasswordRequestDTO {
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password length must be at least 8 characters")
    private String password;
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password length must be at least 8 characters")
    @JsonProperty("new_password")
    private String newPassword;
}
