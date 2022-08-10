package kh.farrukh.progee_api.utils.user;

import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * It's a utility class with methods for user helper logic
 */
public class CurrentUserUtils {

    public static boolean hasPermission(Permission permission, UserRepository userRepository) {
        AppUser currentUser = getCurrentUser(userRepository);
        return currentUser.getRole().getPermissions().contains(permission);
    }

    public static boolean hasPermissionOrIsAuthor(Permission permission, long authorId, UserRepository userRepository) {
        AppUser currentUser = getCurrentUser(userRepository);
        return isAuthor(authorId, userRepository) || currentUser.getRole().getPermissions().contains(permission);
    }

    /**
     * It returns true if the current user's id equals given id
     *
     * @return A boolean value
     */
    public static boolean isAuthor(long authorId, UserRepository userRepository) {
        return getCurrentUser(userRepository).getId() == authorId;
    }

    /**
     * Get the currently logged-in user.
     *
     * @return The user that is currently logged in.
     */
    public static AppUser getCurrentUser(UserRepository userRepository) {
        String email = getEmail();
        return userRepository.findByEmail(email).orElseThrow(
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
