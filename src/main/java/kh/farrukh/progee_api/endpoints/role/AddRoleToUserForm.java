package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.endpoints.user.AppUser;

public class AddRoleToUserForm {
    private Role role;
    private AppUser appUser;

    public AddRoleToUserForm(Role role, AppUser appUser) {
        this.role = role;
        this.appUser = appUser;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AppUser getUser() {
        return appUser;
    }

    public void setUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
