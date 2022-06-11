package kh.farrukh.progee_api.endpoints.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserDTO;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.endpoints.user.UserServiceImpl;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailValidator emailValidator;
    private final UserServiceImpl userService;

    @Override
    public AppUser register(RegistrationRequest registrationRequest) {
        if (!emailValidator.test(registrationRequest.getEmail())) {
            // TODO: 6/7/22 custom exception via exception handler
            throw new RuntimeException("Email is not valid");
        }
        return userService.addUser(
                new AppUserDTO(
                        registrationRequest.getName(),
                        registrationRequest.getEmail(),
                        registrationRequest.getUsername(),
                        registrationRequest.getPassword(),
                        // TODO: 6/9/22 set default to false and implement email verification
                        true,
                        false,
                        UserRole.USER,
                        registrationRequest.getImageId()
                )
        );
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DecodedJWT decodedJWT = JWTUtils.decodeJWT(request);
            if (decodedJWT != null) {
                String username = decodedJWT.getSubject();
                UserDetails user = userService.loadUserByUsername(username);
                Map<String, Object> data = JWTUtils.generateTokens(user, request);
                JWTUtils.sendTokenInResponse(data, response);
            } else {
                // TODO: 6/7/22 custom exception via exception handler
                throw new RuntimeException("Refresh token is missing");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            response.sendError(HttpStatus.FORBIDDEN.value());
        }
    }
}
