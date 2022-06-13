package kh.farrukh.progee_api.endpoints.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserDTO;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.endpoints.user.UserServiceImpl;
import kh.farrukh.progee_api.exception.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * It implements the AuthService interface and uses the EmailValidator and UserServiceImpl classes
 * to register a new user or refresh the token
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailValidator emailValidator;
    private final UserServiceImpl userService;

    /**
     * If the email is valid, add a new user to the database
     *
     * @param registrationRequest The request object that contains the user's information.
     * @return Registered AppUser object
     */
    @Override
    public AppUser register(RegistrationRequest registrationRequest) {
        if (!emailValidator.test(registrationRequest.getEmail())) {
            throw new BadRequestException("Email");
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

    /**
     * It takes the refresh token from the request, decodes it, gets the username from it, loads the user from the
     * database, generates a new access token and refresh token, and sends them back in the response
     *
     * @param request The request object containing refresh token in header
     * @param response The response object that will be used to send the token back to the client.
     */
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
                throw new BadRequestException("Refresh token");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            response.sendError(HttpStatus.FORBIDDEN.value());
        }
    }
}
