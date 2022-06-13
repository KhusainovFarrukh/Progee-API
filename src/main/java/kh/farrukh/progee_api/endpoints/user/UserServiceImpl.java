package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.image.Checkers;
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

import static kh.farrukh.progee_api.utils.image.Checkers.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found in the database")
        );
    }

    @Override
    public PagingResponse<AppUser> getUsers(
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        return new PagingResponse<>(userRepository.findAll(
                PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
        ));
    }

    @Override
    public AppUser getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
    }

    @Override
    public AppUser addUser(AppUserDTO appUserDto) {
        checkUserIsUnique(userRepository, appUserDto);
        Checkers.checkImageId(imageRepository, appUserDto.getImageId());
        AppUser appUser = new AppUser(appUserDto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    @Override
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
        Checkers.checkImageId(imageRepository, appUserDto.getImageId());

        existingAppUser.setName(appUserDto.getName());
        existingAppUser.setEmail(appUserDto.getEmail());
        existingAppUser.setPassword(passwordEncoder.encode(appUserDto.getPassword()));
        existingAppUser.setUniqueUsername(appUserDto.getUsername());
        existingAppUser.setImageId(appUserDto.getImageId());

        return existingAppUser;
    }

    @Override
    public void deleteUser(long id) {
        checkUserId(userRepository, id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public AppUser setUserRole(long id, UserRoleDTO roleDto) {
        AppUser user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
        user.setRole(roleDto.getRole());
        return user;
    }
}
