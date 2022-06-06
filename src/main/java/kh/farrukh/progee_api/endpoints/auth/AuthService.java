package kh.farrukh.progee_api.endpoints.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import kh.farrukh.progee_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void addRoleToUser(String roleTitle, String username) {
        Role role = roleRepository.findByTitle(roleTitle).orElseThrow(
                () -> new ResourceNotFoundException("Role", "title", roleTitle)
        );
        AppUser appUser = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );

        appUser.getRoles().add(role);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DecodedJWT decodedJWT = JWTUtils.decodeJWT(request);
            if (decodedJWT != null) {
                String username = decodedJWT.getSubject();
                AppUser appUser = userRepository.findByUsername(username).orElseThrow(
                        () -> new RuntimeException("User not found")
                );
                Map<String, Object> data = JWTUtils.generateTokens(appUser.toUser(), request);
                JWTUtils.sendTokenInResponse(data, response);
            } else {
                throw new RuntimeException("Refresh token is missing");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            response.sendError(HttpStatus.FORBIDDEN.value());
        }
    }
}
