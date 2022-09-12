package kh.farrukh.progee_api.auth;

import kh.farrukh.progee_api.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.app_user.payloads.AppUserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity<AppUserResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        return ResponseEntity.ok(authService.register(registrationRequestDTO));
    }

    /**
     * Refreshing token
     *
     * @param authHeader The HttpHeader (AUTHORIZATION Header) that contains the token.
     */
    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
        return ResponseEntity.ok(authService.refreshToken(authHeader));
    }
}
