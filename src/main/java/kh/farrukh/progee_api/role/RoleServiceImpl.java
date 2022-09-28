package kh.farrukh.progee_api.role;

import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DefaultRoleDeletionException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static kh.farrukh.progee_api.global.utils.checkers.Checkers.checkPageNumber;
import static kh.farrukh.progee_api.global.utils.checkers.Checkers.checkRoleIsUnique;

/**
 * It implements the RoleService interface and uses the RoleRepository and AppUserRepository to perform CRUD operations on
 * the Role entity
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AppUserRepository appUserRepository;

    /**
     * Get all roles from the database, map them to a DTO, and return them in a paged response
     *
     * @param page     The page number to return.
     * @param pageSize The number of items to return per page.
     * @return A PagingResponse object that contains a list of RoleResponseDTO objects.
     */
    @Override
    public PagingResponse<RoleResponseDTO> getRoles(int page, int pageSize) {
        checkPageNumber(page);
        return new PagingResponse<>(
                roleRepository.findAll(PageRequest.of(page - 1, pageSize))
                        .map(RoleMappers::toRoleResponseDTO)
        );
    }

    /**
     * If the role exists, return the role, otherwise throw an exception
     *
     * @param id The id of the role you want to retrieve.
     * @return A RoleResponseDTO object
     */
    @Override
    public RoleResponseDTO getRoleById(long id) {
        return roleRepository.findById(id)
                .map(RoleMappers::toRoleResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    /**
     * This function adds a role to the database
     *
     * @param roleRequestDto The request object that contains the role name and description.
     * @return A RoleResponseDTO object
     */
    @Override
    public RoleResponseDTO addRole(RoleRequestDTO roleRequestDto) {
        checkRoleIsUnique(roleRepository, roleRequestDto);
        Role role = roleRepository.save(RoleMappers.toRole(roleRequestDto));
        return RoleMappers.toRoleResponseDTO(role);
    }

    /**
     * It updates the role with the given id with the given roleRequestDto
     *
     * @param id             The id of the role to be updated.
     * @param roleRequestDto The request body that contains the new role information.
     * @return A RoleResponseDTO object.
     */
    @Override
    public RoleResponseDTO updateRole(long id, RoleRequestDTO roleRequestDto) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // It checks if the title of the role is changed and if the new title is already taken.
        if (!roleRequestDto.getTitle().equals(existingRole.getTitle()) &&
                roleRepository.existsByTitle(roleRequestDto.getTitle())) {
            throw new DuplicateResourceException("Role", "title", roleRequestDto.getTitle());
        }

        existingRole.setTitle(roleRequestDto.getTitle());
        existingRole.setPermissions(roleRequestDto.getPermissions());

        return RoleMappers.toRoleResponseDTO(roleRepository.save(existingRole));
    }

    /**
     * If the role is the default role, and there is only one default role, then throw an exception. Otherwise, set all
     * users with this role to the default role, and then delete the role
     *
     * @param id The id of the role to delete.
     */
    @Override
    public void deleteRoleById(long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // Checking if the role is the default role and if there is only one default role. If so, then throw an exception.
        if (role.isDefault() && roleRepository.countByIsDefaultIsTrue() <= 1) {
            throw new DefaultRoleDeletionException();
        }

        // Getting the default role and setting all the users with the role to be deleted to the default role.
        Role defaultRole = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "isDefault", true));
        if (role.getUsers() != null) {
            role.getUsers().forEach(user -> {
                user.setRole(defaultRole);
                appUserRepository.save(user);
            });
        }

        roleRepository.deleteById(id);
    }
}
