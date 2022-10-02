package kh.farrukh.progee_api.global.utils.user;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.role.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * It's a utility class with methods for user helper logic
 */
public class CurrentUserUtils {

    /**
     * If the current user is the author of the resource, they need permissionToChangeOwn, otherwise they need
     * permissionToChangeOthers.
     *
     * @param permissionToChangeOthers The permission that the user needs to have to change other users' resources.
     * @param permissionToChangeOwn    The permission that the user needs to have to change their own resources.
     * @param authorId                 The id of the author of the resource you're trying to change.
     * @param appUserRepository        The repository that contains the current user.
     * @return A boolean value.
     */
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

    /**
     * If the current user has the given permission, return true, otherwise return false.
     *
     * @param permission        The permission you want to check for.
     * @param appUserRepository The repository that contains the current user.
     * @return A boolean value.
     */
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
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof User) {
            return ((User) principal).getUsername();
        } else {
            return (String) principal;
        }
    }
}
