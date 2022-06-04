package kh.farrukh.progee_api.endpoints.user;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.endpoints.role.AddRoleToUserForm;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import kh.farrukh.progee_api.endpoints.role.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<AppUser> addUser(@RequestBody AppUser appUser) {
        return new ResponseEntity<>(userService.addUser(appUser), HttpStatus.CREATED);
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> addUser(@RequestBody Role role) {
        return new ResponseEntity<>(userService.addRole(role), HttpStatus.CREATED);
    }

    @PostMapping("/add_role_to_user")
    public ResponseEntity<Void> addRoleToUser(@RequestBody AddRoleToUserForm addRoleToUserForm) {
        userService.addRoleToUser(addRoleToUserForm.getRole().getTitle(), addRoleToUserForm.getUser().getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DecodedJWT decodedJWT = JWTUtils.decodeJWT(request);
            if (decodedJWT != null) {
                String username = decodedJWT.getSubject();
                AppUser appUser = userService.getUserByUsername(username);
                Map<String, Object> data = JWTUtils.generateTokens(appUser.toUser(), request);
                JWTUtils.sendTokenInResponse(data, response);
            } else {
                throw new RuntimeException("Refresh token is missing");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            response.sendError(HttpStatus.FORBIDDEN.value());
        }
    }
}