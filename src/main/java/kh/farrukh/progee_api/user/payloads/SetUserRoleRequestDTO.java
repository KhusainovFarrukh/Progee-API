package kh.farrukh.progee_api.user.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * It's a DTO that represents a role
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetUserRoleRequestDTO {
    @NotNull(message = "Role id must not be null")
    @JsonProperty("role_id")
    private long roleId;
}
