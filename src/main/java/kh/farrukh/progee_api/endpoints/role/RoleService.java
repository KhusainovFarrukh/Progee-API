package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.utils.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role addRole(Role role) {
        if (roleRepository.existsByTitle(role.getTitle())) {
            throw new DuplicateResourceException("Role", "title", role.getTitle());
        }
        return roleRepository.save(role);
    }

}
