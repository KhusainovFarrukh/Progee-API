package kh.farrukh.progee_api.role;

import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static kh.farrukh.progee_api.global.utils.checkers.Checkers.*;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public PagingResponse<RoleResponseDTO> getRoles(int page, int pageSize) {
        checkPageNumber(page);
        return new PagingResponse<>(roleRepository.findAll(
                PageRequest.of(page - 1, pageSize)
        ).map(RoleMappers::toRoleResponseDTO));
    }

    @Override
    public RoleResponseDTO getRoleById(long id) {
        return roleRepository.findById(id)
                .map(RoleMappers::toRoleResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    @Override
    public RoleResponseDTO addRole(RoleRequestDTO roleRequestDto) {
        checkRoleIsUnique(roleRepository, roleRequestDto);
        return RoleMappers.toRoleResponseDTO(roleRepository.save(new Role(roleRequestDto)));
    }

    @Override
    public RoleResponseDTO updateRole(long id, RoleRequestDTO roleRequestDto) {
        Role existingRole = roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id)
        );

        // It checks if the username of the user is changed and if the new username is already taken.
        if (!roleRequestDto.getTitle().equals(existingRole.getTitle()) &&
                roleRepository.existsByTitle(roleRequestDto.getTitle())) {
            throw new DuplicateResourceException("Role", "title", roleRequestDto.getTitle());
        }

        existingRole.setTitle(roleRequestDto.getTitle());
        existingRole.setPermissions(roleRequestDto.getPermissions());

        return RoleMappers.toRoleResponseDTO(roleRepository.save(existingRole));
    }

    @Override
    public void deleteRoleById(long id) {
        checkRoleId(roleRepository, id);
        roleRepository.deleteById(id);
    }
}
