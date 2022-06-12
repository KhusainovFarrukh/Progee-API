package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_USER;

@RestController
@RequestMapping(ENDPOINT_USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PagingResponse<AppUser>> getUsers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return new ResponseEntity<>(
                userService.getUsers(page, pageSize, sortBy, orderBy), HttpStatus.OK
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppUser> addUser(@RequestBody AppUserDTO appUserDto) {
        return new ResponseEntity<>(userService.addUser(appUserDto), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable long id, @RequestBody AppUserDTO appUserDto) {
        return new ResponseEntity<>(userService.updateUser(id, appUserDto), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PutMapping("{id}/role")
    public ResponseEntity<AppUser> setUserRole(
            @PathVariable long id,
            @Valid @RequestBody UserRoleDTO roleDto
    ) {
        return new ResponseEntity<>(userService.setUserRole(id, roleDto), HttpStatus.OK);
    }
}