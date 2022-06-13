package kh.farrukh.progee_api.utils.user;

import kh.farrukh.progee_api.endpoints.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserUtils {

    public static boolean isAdmin() {
        Collection<String> roles = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return roles.contains(UserRole.SUPER_ADMIN.name()) || roles.contains(UserRole.ADMIN.name());
    }

    public static String getEmail() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
