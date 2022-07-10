package kh.farrukh.progee_api.endpoints.auth;

import kh.farrukh.progee_api.endpoints.user.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_REGISTRATION;

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
     * @param registrationRequest The request object that contains new user details.
     * @return A ResponseEntity with the AppUser object and a status of OK.
     */
    @PostMapping(ENDPOINT_REGISTRATION)
    public ResponseEntity<AppUser> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(authService.register(registrationRequest), HttpStatus.OK);
    }

    /**
     * Refreshing token
     *
     * @param request The request object that contains the token.
     */
    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) throws IOException {
        return new ResponseEntity<>(authService.refreshToken(request), HttpStatus.OK);
    }
}
