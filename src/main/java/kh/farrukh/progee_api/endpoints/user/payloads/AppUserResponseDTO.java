package kh.farrukh.progee_api.endpoints.user.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserResponseDTO {

    private String name;

    private String email;

    @JsonProperty("username")
    private String uniqueUsername;

    @JsonProperty("is_enabled")
    private boolean isEnabled;

    @JsonProperty("is_locked")
    private boolean isLocked;

    private Role role;

    private Image image;
}
