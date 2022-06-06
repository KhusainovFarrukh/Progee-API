package kh.farrukh.progee_api.endpoints.auth;

import kh.farrukh.progee_api.endpoints.role.AddRoleToUserForm;
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

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_ROLE_TO_USER;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(ENDPOINT_ROLE_TO_USER)
    public ResponseEntity<Void> addRoleToUser(@RequestBody AddRoleToUserForm addRoleToUserForm) {
        authService.addRoleToUser(addRoleToUserForm.getRole().getTitle(), addRoleToUserForm.getAppUser().getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}
