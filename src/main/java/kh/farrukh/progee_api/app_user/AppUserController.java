package kh.farrukh.progee_api.app_user;

import kh.farrukh.progee_api.app_user.payloads.*;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.app_user.AppUserConstants.ENDPOINT_USER;

/**
 * Controller that exposes endpoints for managing users
 */
@RestController
@RequestMapping(ENDPOINT_USER)
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

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
    public ResponseEntity<PagingResponse<AppUserResponseDTO>> getUsers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return ResponseEntity.ok(appUserService.getUsers(page, pageSize, sortBy, orderBy));
    }

    /**
     * It returns a user by id.
     *
     * @param id The id of the user you want to get.
     * @return A ResponseEntity with found AppUser.
     */
    @GetMapping("{id}")
    public ResponseEntity<AppUserResponseDTO> getUserById(@PathVariable long id) {
        return ResponseEntity.ok(appUserService.getUserById(id));
    }

    /**
     * This function updates a user.
     *
     * @param id                The id of the user to update
     * @param appUserRequestDto The user values that we want to update.
     * @return A ResponseEntity with the updated AppUser object and HttpStatus.
     */
    @PutMapping("{id}")
    public ResponseEntity<AppUserResponseDTO> updateUser(
            @PathVariable long id,
            @RequestBody AppUserRequestDTO appUserRequestDto
    ) {
        return ResponseEntity.ok(appUserService.updateUser(id, appUserRequestDto));
    }

    /**
     * This function deletes a user from a language
     *
     * @param id The id of the user to delete
     * @return A ResponseEntity with HttpStatus.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<AppUserResponseDTO> setUserRole(
            @PathVariable long id,
            @Valid @RequestBody SetUserRoleRequestDTO roleDto
    ) {
        return ResponseEntity.ok(appUserService.setUserRole(id, roleDto));
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
    public ResponseEntity<AppUserResponseDTO> setUserImage(
            @PathVariable long id,
            @Valid @RequestBody SetUserImageRequestDTO imageDto
    ) {
        return ResponseEntity.ok(appUserService.setUserImage(id, imageDto));
    }

    /**
     * This function sets the password of the user to the given new password
     *
     * @param id          The id of the user to be updated
     * @param passwordDto This is the object that contains the current and new passwords.
     * @return A ResponseEntity with the updated AppUser object and HttpStatus.
     */
    @PatchMapping("{id}/password")
    public ResponseEntity<AppUserResponseDTO> setUserPassword(
            @PathVariable long id,
            @Valid @RequestBody SetUserPasswordRequestDTO passwordDto
    ) {
        return ResponseEntity.ok(appUserService.setUserPassword(id, passwordDto));
    }
}