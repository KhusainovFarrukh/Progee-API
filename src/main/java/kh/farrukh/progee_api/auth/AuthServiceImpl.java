package kh.farrukh.progee_api.auth;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.token_exceptions.*;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.app_user.AppUserMappers;
import kh.farrukh.progee_api.app_user.AppUserService;
import kh.farrukh.progee_api.app_user.payloads.AppUserResponseDTO;
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
    private final AppUserService appUserService;
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
        return appUserService.addUser(AuthMappers.toAppUserRequestDTO(registrationRequestDTO, roleRepository));
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
            String username = decodedJWT.getSubject();
            AppUserResponseDTO user = appUserService.getUserByEmail(username);
            return tokenProvider.generateTokens(AppUserMappers.toAppUser(user));
        } catch (AlgorithmMismatchException exception) {
            throw new WrongTypeTokenException();
        } catch (SignatureVerificationException exception) {
            throw new InvalidSignatureTokenException();
        } catch (TokenExpiredException exception) {
            throw new ExpiredTokenException();
        } catch (InvalidClaimException exception) {
            throw new InvalidRoleTokenException();
        } catch (MissingTokenException exception) {
            throw new MissingTokenException();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new UnknownTokenException();
        }
    }
}
