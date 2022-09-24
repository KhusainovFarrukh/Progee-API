package kh.farrukh.progee_api.auth;

import kh.farrukh.progee_api.app_user.payloads.AppUserResponseDTO;
import kh.farrukh.progee_api.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.auth.payloads.RegistrationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.auth.AuthConstants.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.progee_api.auth.AuthConstants.ENDPOINT_REGISTRATION;

/**
 * Controller for Auth-related endpoints (login, register, refresh token and etc.)
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registering new user
     *
     * @param registrationRequestDTO The request object that contains new user details.
     * @return A ResponseEntity with the AppUserResponseDTO object and a status of OK.
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
