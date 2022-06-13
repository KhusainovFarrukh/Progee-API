package kh.farrukh.progee_api.utils.user;

import kh.farrukh.progee_api.endpoints.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * It's a utility class with methods for user helper logic
 */
public class UserUtils {

    /**
     * It returns true if the current user is an admin or super admin
     *
     * @return A boolean value
     */
    public static boolean isAdmin() {
        Collection<String> roles = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return roles.contains(UserRole.SUPER_ADMIN.name()) || roles.contains(UserRole.ADMIN.name());
    }

    /**
     * Get the email of the currently logged-in user.
     *
     * @return The email of the user that is currently logged in.
     */
    public static String getEmail() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
