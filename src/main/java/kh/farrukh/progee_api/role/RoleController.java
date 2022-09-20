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

@RestController
@RequestMapping(ENDPOINT_ROLE)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<PagingResponse<RoleResponseDTO>> getRoles(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(roleService.getRoles(page, pageSize));
    }

    @GetMapping("{id}")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<RoleResponseDTO> addRole(@Valid @RequestBody RoleRequestDTO roleRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roleService.addRole(roleRequestDto));
    }

    @PutMapping("{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable long id,
            @Valid @RequestBody RoleRequestDTO roleRequestDto
    ) {
        return ResponseEntity.ok(roleService.updateRole(id, roleRequestDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.noContent().build();
    }
}