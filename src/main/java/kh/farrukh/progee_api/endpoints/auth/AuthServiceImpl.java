package kh.farrukh.progee_api.endpoints.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.endpoints.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.endpoints.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.endpoints.user.UserService;
import kh.farrukh.progee_api.endpoints.user.payloads.AppUserResponseDTO;
import kh.farrukh.progee_api.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * It implements the AuthService interface and uses the EmailValidator and UserServiceImpl classes
 * to register a new user or refresh the token
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenProvider tokenProvider;
    private final EmailValidator emailValidator;
    private final UserService userService;
    private final RoleRepository roleRepository;

    /**
     * If the email is valid, add a new user to the database
     *
     * @param registrationRequestDTO The request object that contains the user's information.
     * @return Registered AppUser object
     */
    @Override
    public AppUserResponseDTO register(RegistrationRequestDTO registrationRequestDTO) {
        if (!emailValidator.test(registrationRequestDTO.getEmail())) {
            throw new BadRequestException("Email");
        }
        return userService.addUser(new AppUserRequestDTO(registrationRequestDTO, roleRepository));
    }

    /**
     * It takes the refresh token from the request, decodes it, gets the username from it, loads the user from the
     * database, generates a new access token and refresh token, and sends them back in the response
     *
     * @param authHeader The refresh token in header
     */
    @Override
    public AuthResponseDTO refreshToken(String authHeader) {
        try {
            DecodedJWT decodedJWT = tokenProvider.validateToken(authHeader, true);
            if (decodedJWT != null) {
                String username = decodedJWT.getSubject();
                AppUser user = userService.getUserByEmail(username);
                return tokenProvider.generateTokens(user);
            } else {
                throw new BadRequestException("Refresh token");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BadRequestException("Refresh token");
        }
    }
}
