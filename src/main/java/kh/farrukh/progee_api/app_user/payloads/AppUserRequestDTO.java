package kh.farrukh.progee_api.app_user.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * It's a DTO class that contains all the fields that are required to create a new user
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRequestDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "Email must not be blank")
    private String email;
    @JsonProperty("username")
    @NotBlank(message = "Username must not be blank")
    @Size(max = 16, message = "Username must be shorter than 16 characters")
    private String uniqueUsername;
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password length must be at least 8 characters")
    private String password;
    @JsonProperty("is_enabled")
    private boolean isEnabled = true;
    @JsonProperty("is_locked")
    private boolean isLocked = false;
    @JsonProperty("role_id")
    @NotNull(message = "Role id must not be null")
    private long roleId;
    @JsonProperty("image_id")
    private long imageId;
}
