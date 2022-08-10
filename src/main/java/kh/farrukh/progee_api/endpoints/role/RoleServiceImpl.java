package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static kh.farrukh.progee_api.utils.checkers.Checkers.*;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public PagingResponse<Role> getRoles(int page, int pageSize) {
        checkPageNumber(page);
        return new PagingResponse<>(roleRepository.findAll(
                PageRequest.of(page - 1, pageSize)
        ));
    }

    @Override
    public Role getRoleById(long id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id)
        );
    }

    @Override
    public Role addRole(RoleDTO roleDto) {
        checkRoleIsUnique(roleRepository, roleDto);
        return roleRepository.save(new Role(roleDto));
    }

    @Override
    public Role updateRole(long id, RoleDTO roleDto) {
        Role existingRole = roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id)
        );

        // It checks if the username of the user is changed and if the new username is already taken.
        if (!roleDto.getTitle().equals(existingRole.getTitle()) &&
                roleRepository.existsByTitle(roleDto.getTitle())) {
            throw new DuplicateResourceException("Role", "title", roleDto.getTitle());
        }

        existingRole.setTitle(roleDto.getTitle());
        existingRole.setPermissions(roleDto.getPermissions());

        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteRoleById(long id) {
        checkRoleId(roleRepository, id);
        roleRepository.deleteById(id);
    }
}
