package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.utils.exception.DuplicateResourceException;
import kh.farrukh.progee_api.utils.exception.ResourceNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found in the database")
        );

        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                appUser.getRoles().stream().map(
                        (role) -> new SimpleGrantedAuthority(role.getTitle())
                ).collect(Collectors.toList())
        );
    }

    @Transactional
    public void addRoleToUser(String roleTitle, String username) {
        Role role = roleRepository.findByTitle(roleTitle).orElseThrow(
                () -> new ResourceNotFoundException("Role", "title", roleTitle)
        );
        AppUser appUser = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );

        appUser.getRoles().add(role);
    }

    public List<AppUser> getUsers() {
        return userRepository.findAll();
    }

    public AppUser getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
    }

    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
    }

    public AppUser addUser(AppUser appUser) {
        if (userRepository.existsByUsername(appUser.getUsername())) {
            throw new DuplicateResourceException("User", "username", appUser.getUsername());
        }
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    public Role addRole(Role role) {
        if (roleRepository.existsByTitle(role.getTitle())) {
            throw new DuplicateResourceException("Role", "title", role.getTitle());
        }
        return roleRepository.save(role);
    }

    @Transactional
    public AppUser updateUser(long id, AppUser appUser) {
        AppUser existingAppUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        existingAppUser.setFirstName(appUser.getFirstName());
        existingAppUser.setLastName(appUser.getLastName());
        existingAppUser.setUsername(appUser.getUsername());
        existingAppUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        existingAppUser.setRoles(appUser.getRoles());

        return existingAppUser;
    }

    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }
}
