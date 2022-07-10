package kh.farrukh.progee_api.endpoints.auth;

import kh.farrukh.progee_api.endpoints.user.AppUser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A base interface for service of Auth endpoints
 *
 * Methods implemented in AuthServiceImpl
 */
public interface AuthService {

    AppUser register(RegistrationRequest registrationRequest);

    AuthResponse refreshToken(HttpServletRequest request) throws IOException;
}
