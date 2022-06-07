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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(ENDPOINT_REGISTRATION)
    public ResponseEntity<AppUser> register(@RequestBody RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(authService.register(registrationRequest), HttpStatus.OK);
    }

    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}
