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

    public AppUser addUser(AppUserDTO appUserDto) {
        AppUser appUser = new AppUser(appUserDto);
        checkIsUnique(appUser);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    @Transactional
    public AppUser updateUser(long id, AppUserDTO appUserDto) {
        AppUser existingAppUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        if (!appUserDto.getUsername().equals(existingAppUser.getUniqueUsername()) &&
                userRepository.existsByUniqueUsername(appUserDto.getUsername())) {
            throw new DuplicateResourceException("User", "username", appUserDto.getUsername());
        }

        if (!appUserDto.getEmail().equals(existingAppUser.getEmail()) &&
                userRepository.existsByEmail(appUserDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", appUserDto.getEmail());
        }

        existingAppUser.setName(appUserDto.getName());
        existingAppUser.setEmail(appUserDto.getEmail());
        existingAppUser.setPassword(passwordEncoder.encode(appUserDto.getPassword()));
        existingAppUser.setEnabled(appUserDto.isEnabled());
        existingAppUser.setLocked(appUserDto.isLocked());
        existingAppUser.setUniqueUsername(appUserDto.getUsername());
        existingAppUser.setRole(appUserDto.getRole());
        existingAppUser.setImageId(appUserDto.getImageId());

        return existingAppUser;
    }

    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
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
