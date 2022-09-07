package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.endpoints.role.RoleController.ENDPOINT_ROLE;

@RestController
@RequestMapping(ENDPOINT_ROLE)
@RequiredArgsConstructor
public class RoleController {

    public static final String ENDPOINT_ROLE = "/api/v1/roles";

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<PagingResponse<Role>> getRoles(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize
    ) {
        return new ResponseEntity<>(roleService.getRoles(page, pageSize), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable long id) {
        return new ResponseEntity<>(roleService.getRoleById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Role> addRole(@Valid @RequestBody RoleDTO roleDto) {
        return new ResponseEntity<>(roleService.addRole(roleDto), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Role> updateRole(
            @PathVariable long id,
            @Valid @RequestBody RoleDTO roleDto
    ) {
        return new ResponseEntity<>(roleService.updateRole(id, roleDto), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable long id) {
        roleService.deleteRoleById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
