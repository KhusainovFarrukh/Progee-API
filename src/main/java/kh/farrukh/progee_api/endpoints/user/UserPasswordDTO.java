package kh.farrukh.progee_api.endpoints.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordDTO {
    @NotNull
    private String password;
    @NotNull
    @JsonProperty("new_password")
    private String newPassword;
}
