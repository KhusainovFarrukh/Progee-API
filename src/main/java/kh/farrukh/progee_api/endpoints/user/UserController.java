package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_USER;

/**
 * Controller that exposes endpoints for managing users
 */
@RestController
@RequestMapping(ENDPOINT_USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * It returns a list of users.
     *
     * @param page     The page number to return.
     * @param pageSize The number of items to be returned in a page.
     * @param sortBy   The field to sort by.
     * @param orderBy  asc or desc
     * @return A ResponseEntity with a PagingResponse of AppUser objects.
     */
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

    /**
     * It returns a user by id.
     *
     * @param id The id of the user you want to get.
     * @return A ResponseEntity with found AppUser.
     */
    @GetMapping("{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    /**
     * This function updates a user.
     *
     * @param id         The id of the user to update
     * @param appUserDto The user values that we want to update.
     * @return A ResponseEntity with the updated AppUser object and HttpStatus.
     */
    @PutMapping("{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable long id, @RequestBody AppUserDTO appUserDto) {
        return new ResponseEntity<>(userService.updateUser(id, appUserDto), HttpStatus.OK);
    }

    /**
     * This function deletes a user from a language
     *
     * @param id The id of the user to delete
     * @return A ResponseEntity with HttpStatus.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This function sets the role of the user with the given id,
     * to the role given in the request body.
     *
     * @param id      The id of the user to be updated
     * @param roleDto This is the object that contains the role that we want to set the user to.
     * @return A ResponseEntity with the updated AppUser object and HttpStatus.
     */
    @PatchMapping("{id}/role")
    public ResponseEntity<AppUser> setUserRole(
            @PathVariable long id,
            @Valid @RequestBody UserRoleDTO roleDto
    ) {
        return new ResponseEntity<>(userService.setUserRole(id, roleDto), HttpStatus.OK);
    }

    /**
     * This function sets the image of the user with the given id,
     * to the image given in the request body.
     *
     * @param id       The id of the user to be updated
     * @param imageDto This is the object that contains the image id that we want to set the user to.
     * @return A ResponseEntity with the updated AppUser object and HttpStatus.
     */
    @PatchMapping("{id}/image")
    public ResponseEntity<AppUser> setUserImage(
            @PathVariable long id,
            @Valid @RequestBody UserImageDTO imageDto
    ) {
        return new ResponseEntity<>(userService.setUserImage(id, imageDto), HttpStatus.OK);
    }
}