package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.exception.DuplicateResourceException;
import kh.farrukh.progee_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(long id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id)
        );
    }

    public Role addRole(Role role) {
        if (roleRepository.existsByTitle(role.getTitle())) {
            throw new DuplicateResourceException("Role", "title", role.getTitle());
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(long id, Role role) {
        Role existingRole = roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id)
        );

        existingRole.setTitle(role.getTitle());

        return existingRole;
    }

    public void deleteRole(long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.deleteById(id);
    }
}
