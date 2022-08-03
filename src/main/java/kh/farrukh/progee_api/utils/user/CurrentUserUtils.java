package kh.farrukh.progee_api.utils.user;

import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * It's a utility class with methods for user helper logic
 */
public class CurrentUserUtils {

    /**
     * It returns true if the current user's id equals given id or current user is admin
     *
     * @return A boolean value
     */
    public static boolean isAdminOrAuthor(long authorId, UserRepository userRepository) {
        AppUser currentUser = getCurrentUser(userRepository);
        return currentUser.isAdmin() || currentUser.getId() == authorId;
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
     * It returns true if the current user is an admin or super admin
     *
     * @return A boolean value
     */
    public static boolean isAdmin() {
        try {
            Collection<String> roles = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority).toList();

            return roles.contains(UserRole.SUPER_ADMIN.name()) || roles.contains(UserRole.ADMIN.name());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
