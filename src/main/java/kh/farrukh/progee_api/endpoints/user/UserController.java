package kh.farrukh.progee_api.endpoints.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_USER;

@RestController
@RequestMapping(ENDPOINT_USER)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppUser> addUser(@RequestBody AppUser appUser) {
        return new ResponseEntity<>(userService.addUser(appUser), HttpStatus.CREATED);
    }
}