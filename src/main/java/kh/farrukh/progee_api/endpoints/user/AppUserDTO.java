package kh.farrukh.progee_api.endpoints.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.auth.RegistrationRequest;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * It's a DTO that represents a user
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "Email must not be blank")
    private String email;
    @NotBlank(message = "Username must not be blank")
    @Size(max = 16, message = "Username must be shorter than 16 characters")
    private String username;
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

    public AppUserDTO(RegistrationRequest request, RoleRepository roleRepository) {
        this.name = request.getName();
        this.email = request.getEmail();
        this.username = request.getUsername();
        this.password = request.getPassword();
        // TODO: 6/9/22 set default to false and implement email verification
        this.isEnabled = true;
        this.isLocked = false;
        this.roleId = roleRepository.findFirstByIsDefaultIsTrue().orElseThrow(
                () -> new ResourceNotFoundException("Role", "isDefault", true)
        ).getId();
        this.imageId = request.getImageId();
    }
}
