package kh.farrukh.progee_api.endpoints.auth;

import kh.farrukh.progee_api.endpoints.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.endpoints.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.endpoints.user.AppUser;

import java.io.IOException;

/**
 * A base interface for service of Auth endpoints
 * <p>
 * Methods implemented in AuthServiceImpl
 */
public interface AuthService {

    AppUser register(RegistrationRequestDTO registrationRequestDTO);

    AuthResponseDTO refreshToken(String authHeader) throws IOException;
}