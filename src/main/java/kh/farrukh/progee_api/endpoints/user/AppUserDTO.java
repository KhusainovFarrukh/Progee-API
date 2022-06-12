package kh.farrukh.progee_api.endpoints.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
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
