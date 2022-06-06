package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.endpoints.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoleToUserForm {
    private Role role;
    private AppUser appUser;
}
