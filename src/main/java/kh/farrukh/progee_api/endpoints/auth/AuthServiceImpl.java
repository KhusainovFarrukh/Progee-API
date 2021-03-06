package kh.farrukh.progee_api.endpoints.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserDTO;
import kh.farrukh.progee_api.endpoints.user.UserService;
import kh.farrukh.progee_api.exception.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * It implements the AuthService interface and uses the EmailValidator and UserServiceImpl classes
 * to register a new user or refresh the token
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailValidator emailValidator;
    private final UserService userService;

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
        return userService.addUser(new AppUserDTO(registrationRequest));
    }

    /**
     * It takes the refresh token from the request, decodes it, gets the username from it, loads the user from the
     * database, generates a new access token and refresh token, and sends them back in the response
     *
     * @param authHeader The refresh token in header
     */
    @Override
    public AuthResponse refreshToken(String authHeader) {
        try {
            DecodedJWT decodedJWT = JWTUtils.decodeJWT(authHeader);
            String username = decodedJWT.getSubject();
            UserDetails user = userService.loadUserByUsername(username);
            return JWTUtils.generateTokens(user);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BadRequestException("Refresh token");
        }
    }
}
