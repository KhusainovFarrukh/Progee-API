package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role addRole(Role role) {
        if (roleRepository.existsByTitle(role.getTitle())) {
            throw new DuplicateResourceException("Role", "title", role.getTitle());
        }
        return roleRepository.save(role);
    }

}
