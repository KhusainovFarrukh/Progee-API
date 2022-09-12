package kh.farrukh.progee_api.app_user.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserResponseDTO {

    private long id;

    private String name;

    private String email;

    @JsonProperty("username")
    private String uniqueUsername;

    @JsonProperty("is_enabled")
    private boolean isEnabled;

    @JsonProperty("is_locked")
    private boolean isLocked;

    private RoleResponseDTO role;

    private ImageResponseDTO image;
}
