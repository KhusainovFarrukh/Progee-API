package kh.farrukh.progee_api.role;

import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.role.RoleConstants.ENDPOINT_ROLE;

/**
 * It's a REST controller that exposes endpoints for CRUD operations on the Role entity
 */
@RestController
@RequestMapping(ENDPOINT_ROLE)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Get all roles with pagination
     *
     * @param page The page number.
     * @param pageSize The number of items to be displayed on a page.
     * @return A list of roles
     */
    @GetMapping
    public ResponseEntity<PagingResponse<RoleResponseDTO>> getRoles(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(roleService.getRoles(page, pageSize));
    }

    /**
     * Returns a role by id
     *
     * @param id The id of the role you want to get.
     * @return A ResponseEntity containing RoleResponseDTO object and HttpStatus.
     */
    @GetMapping("{id}")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    /**
     * Creates role if it does not exist.
     *
     * @param roleRequestDto Values for the role to be created.
     * @return A ResponseEntity containing created RoleResponseDTO object and HttpStatus.
     */
    @PostMapping
    public ResponseEntity<RoleResponseDTO> addRole(@Valid @RequestBody RoleRequestDTO roleRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roleService.addRole(roleRequestDto));
    }

    /**
     * Updates a language.
     *
     * @param id                 The id of the role to update
     * @param roleRequestDto The role values that we want to update.
     * @return A ResponseEntity with the updated RoleResponseDTO object and HttpStatus.
     */
    @PutMapping("{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable long id,
            @Valid @RequestBody RoleRequestDTO roleRequestDto
    ) {
        return ResponseEntity.ok(roleService.updateRole(id, roleRequestDto));
    }

    /**
     * Deletes a role by id.
     *
     * @param id The id of the role to be deleted.
     * @return A ResponseEntity with no content.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.noContent().build();
    }
}