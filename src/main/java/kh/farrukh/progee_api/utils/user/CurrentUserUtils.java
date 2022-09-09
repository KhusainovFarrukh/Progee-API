package kh.farrukh.progee_api.utils.user;

import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserRepository;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * It's a utility class with methods for user helper logic
 */
public class CurrentUserUtils {

    public static boolean hasPermissionOrIsAuthor(
            Permission permissionToChangeOthers,
            Permission permissionToChangeOwn,
            long authorId,
            AppUserRepository appUserRepository
    ) {
        try {
            AppUser currentUser = getCurrentUser(appUserRepository);
            return (currentUser.getId() != authorId && currentUser.getRole().getPermissions().contains(permissionToChangeOthers)) ||
                    (currentUser.getId() == authorId && currentUser.getRole().getPermissions().contains(permissionToChangeOwn));
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public static boolean hasPermission(Permission permission, AppUserRepository appUserRepository) {
        try {
            AppUser currentUser = getCurrentUser(appUserRepository);
            return currentUser.getRole().getPermissions().contains(permission);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    /**
     * Get the currently logged-in user.
     *
     * @return The user that is currently logged in.
     */
    public static AppUser getCurrentUser(AppUserRepository appUserRepository) {
        String email = getEmail();
        return appUserRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );
    }

    /**
     * Get the email of the currently logged-in user.
     *
     * @return The email of the user that is currently logged in.
     */
    private static String getEmail() {
        try {
            Object principal = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            if (principal instanceof User) {
                return ((User) principal).getUsername();
            } else {
                return (String) principal;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
