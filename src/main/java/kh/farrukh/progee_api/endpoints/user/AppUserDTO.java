package kh.farrukh.progee_api.endpoints.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

/**
 * It's a DTO that represents a user
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO {

    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @JsonProperty("is_enabled")
    private boolean isEnabled = true;
    @JsonProperty("is_locked")
    private boolean isLocked = false;
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;
    @JsonProperty("image_id")
    private long imageId;
}
