package kh.farrukh.progee_api.endpoints.auth;

import kh.farrukh.progee_api.endpoints.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.endpoints.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Controller for Auth-related endpoints (login, register, refresh token and etc.)
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    public static final String ENDPOINT_REGISTRATION = "/api/v1/registration";
    public static final String ENDPOINT_REFRESH_TOKEN = "/api/v1/token/refresh";

    private final AuthService authService;

    /**
     * Registering new user
     *
     * @param registrationRequestDTO The request object that contains new user details.
     * @return A ResponseEntity with the AppUser object and a status of OK.
     */
    @PostMapping(ENDPOINT_REGISTRATION)
    public ResponseEntity<AppUser> register(@Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        return new ResponseEntity<>(authService.register(registrationRequestDTO), HttpStatus.OK);
    }

    /**
     * Refreshing token
     *
     * @param authHeader The HttpHeader (AUTHORIZATION Header) that contains the token.
     */
    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) throws IOException {
        return new ResponseEntity<>(
                authService.refreshToken(authHeader), HttpStatus.OK
        );
    }
}
