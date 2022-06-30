package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.checkers.Checkers;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static kh.farrukh.progee_api.utils.checkers.Checkers.*;

/**
 * It implements the UserService interface and the UserDetailsService interface,
 * uses the UserRepository, PasswordEncoder and ImageRepository to perform CRUD operations on the AppUser entity
 * <p>
 * Implements UserDetailsService to be used in Spring Security.
 * It means that this class is injected as bean dependency to Spring Security Configurations
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;

    /**
     * If the user exists in the database, return the user, otherwise throw an exception.
     *
     * @param username The username of the user we're trying to authenticate.
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found in the database")
        );
    }

    /**
     * "Get all users from the database, sort them by the given sortBy and orderBy parameters, and return a PagingResponse
     * object containing the users in the given page."
     *
     * @param page     The page number to return.
     * @param pageSize The number of items to return per page.
     * @param sortBy   The field to sort by.
     * @param orderBy  The direction of the sort. Can be either "asc" or "desc".
     * @return A PagingResponse object.
     */
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

    /**
     * If the user exists, return the user, otherwise throw an exception.
     *
     * @param id The id of the user to retrieve
     * @return The userRepository.findById(id) is being returned.
     */
    @Override
    public AppUser getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
    }

    /**
     * Creates a new AppUser object, encodes the password, and saves the user
     *
     * @param appUserDto The DTO object that contains the user's information.
     * @return The created user.
     */
    @Override
    public AppUser addUser(AppUserDTO appUserDto) {
        checkUserIsUnique(userRepository, appUserDto);
        checkImageId(imageRepository, appUserDto.getImageId());
        AppUser appUser = new AppUser(appUserDto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    /**
     * If the username or email is different from the existing one, check if it exists in the database. If it does, throw
     * an exception. If it doesn't, update the user
     *
     * @param id         The id of the user to be updated.
     * @param appUserDto The DTO object that contains the new values for the user.
     * @return The updated user.
     */
    @Override
    @Transactional
    public AppUser updateUser(long id, AppUserDTO appUserDto) {
        AppUser existingAppUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        // It checks if the username of the user is changed and if the new username is already taken.
        if (!appUserDto.getUsername().equals(existingAppUser.getUniqueUsername()) &&
                userRepository.existsByUniqueUsername(appUserDto.getUsername())) {
            throw new DuplicateResourceException("User", "username", appUserDto.getUsername());
        }

        // It checks if the email of the user is changed and if the new email is already taken.
        if (!appUserDto.getEmail().equals(existingAppUser.getEmail()) &&
                userRepository.existsByEmail(appUserDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", appUserDto.getEmail());
        }
        Checkers.checkImageId(imageRepository, appUserDto.getImageId());

        existingAppUser.setName(appUserDto.getName());
        existingAppUser.setEmail(appUserDto.getEmail());
        existingAppUser.setUniqueUsername(appUserDto.getUsername());
        existingAppUser.setImageId(appUserDto.getImageId());

        return existingAppUser;
    }

    /**
     * It deletes a user from the database
     *
     * @param id The id of the user to be deleted.
     */
    @Override
    public void deleteUser(long id) {
        checkUserId(userRepository, id);
        userRepository.deleteById(id);
    }

    /**
     * It takes in a user id and a UserRoleDTO object, finds the user in the database,
     * sets the user's role to the role in the UserRoleDTO object, and returns the user
     *
     * @param id      The id of the user to be updated
     * @param roleDto This is the object that will be passed in the request body.
     * @return The updated user.
     */
    @Override
    @Transactional
    public AppUser setUserRole(long id, UserRoleDTO roleDto) {
        AppUser user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
        user.setRole(roleDto.getRole());
        return user;
    }

    /**
     * It takes in a user id and a UserImageDTO object, finds the user in the database,
     * sets the user's image to the image with id in the UserImageDTO object, and returns the user
     *
     * @param id       The id of the user to be updated
     * @param imageDto This is the object that will be passed in the request body.
     * @return The updated user.
     */
    @Override
    @Transactional
    public AppUser setUserImage(long id, UserImageDTO imageDto) {
        if (CurrentUserUtils.isAdminOrAuthor(id, userRepository)) {
            AppUser user = userRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", id)
            );

            checkImageId(imageRepository, imageDto.getImageId());
            user.setImageId(imageDto.getImageId());
            return user;
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * It takes in a user id and a UserPasswordDTO object, finds the user in the database,
     * checks the current password, sets the user's password to the password in the UserPasswordDTO object,
     * and returns the user
     *
     * @param id          The id of the user to be updated
     * @param passwordDto This is the object that will be passed in the request body.
     * @return The updated user.
     */
    @Override
    @Transactional
    public AppUser setUserPassword(long id, UserPasswordDTO passwordDto) {
        if (CurrentUserUtils.isAdminOrAuthor(id, userRepository)) {
            AppUser currentUser = userRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", id)
            );
            if (passwordEncoder.matches(passwordDto.getPassword(), currentUser.getPassword())) {
                currentUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
                return currentUser;
            } else {
                throw new BadRequestException("Password");
            }
        } else {
            throw new NotEnoughPermissionException();
        }
    }
}
