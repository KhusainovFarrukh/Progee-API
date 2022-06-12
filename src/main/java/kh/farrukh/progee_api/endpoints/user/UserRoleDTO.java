package kh.farrukh.progee_api.endpoints.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO {
    @NotNull
    private UserRole role;
}
