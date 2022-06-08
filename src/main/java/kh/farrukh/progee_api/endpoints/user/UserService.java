package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.exception.DuplicateResourceException;
import kh.farrukh.progee_api.exception.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found in the database")
        );
    }

    public PagingResponse<AppUser> getUsers(
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        return new PagingResponse<>(userRepository.findAll(
                PageRequest.of(page, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
        ));
    }

    public AppUser getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
    }

    public AppUser addUser(AppUser appUser) {
        checkIsUnique(appUser);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    @Transactional
    public AppUser updateUser(long id, AppUser appUser) {
        AppUser existingAppUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        existingAppUser.setName(appUser.getName());
        existingAppUser.setEmail(appUser.getEmail());
        existingAppUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        existingAppUser.setEnabled(appUser.isEnabled());
        existingAppUser.setLocked(appUser.isLocked());
        existingAppUser.setUniqueUsername(appUser.getUniqueUsername());
        existingAppUser.setRole(appUser.getRole());

        return existingAppUser;
    }

    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    public AppUser signUpUser(AppUser appUser) {
        checkIsUnique(appUser);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        // TODO: 6/7/22 email verification
        return userRepository.save(appUser);
    }

    private void checkIsUnique(AppUser appUser) {
        if (userRepository.existsByUniqueUsername(appUser.getUniqueUsername())) {
            throw new DuplicateResourceException("User", "username", appUser.getUniqueUsername());
        }
        if (userRepository.existsByEmail(appUser.getEmail())) {
            throw new DuplicateResourceException("User", "email", appUser.getUsername());
        }
    }
}
