package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.endpoints.user.payloads.*;
import kh.farrukh.progee_api.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;
    private final RoleRepository roleRepository;

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
     * If the user exists, return the user, otherwise throw an exception.
     *
     * @param email The email of the user to retrieve
     * @return The userRepository.findByEmail(email) is being returned.
     */
    @Override
    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );
    }

    /**
     * Creates a new AppUser object, encodes the password, and saves the user
     *
     * @param appUserRequestDto The DTO object that contains the user's information.
     * @return The created user.
     */
    @Override
    public AppUserResponseDTO addUser(AppUserRequestDTO appUserRequestDto) {
        checkUserIsUnique(userRepository, appUserRequestDto);
        AppUser appUser = new AppUser(appUserRequestDto, imageRepository, roleRepository);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return AppUserMappers.toAppUserResponseDTO(userRepository.save(appUser));
    }

    /**
     * If the username or email is different from the existing one, check if it exists in the database. If it does, throw
     * an exception. If it doesn't, update the user
     *
     * @param id                The id of the user to be updated.
     * @param appUserRequestDto The DTO object that contains the new values for the user.
     * @return The updated user.
     */
    @Override
    public AppUser updateUser(long id, AppUserRequestDTO appUserRequestDto) {
        AppUser user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        if (
                CurrentUserUtils.hasPermissionOrIsAuthor(
                        Permission.CAN_UPDATE_OTHER_USER,
                        Permission.CAN_UPDATE_OWN_USER,
                        user.getId(),
                        userRepository
                )
        ) {

            // It checks if the username of the user is changed and if the new username is already taken.
            if (!appUserRequestDto.getUsername().equals(user.getUniqueUsername()) &&
                    userRepository.existsByUniqueUsername(appUserRequestDto.getUsername())) {
                throw new DuplicateResourceException("User", "username", appUserRequestDto.getUsername());
            }

            // It checks if the email of the user is changed and if the new email is already taken.
            if (!appUserRequestDto.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(appUserRequestDto.getEmail())) {
                throw new DuplicateResourceException("User", "email", appUserRequestDto.getEmail());
            }

            user.setName(appUserRequestDto.getName());
            user.setEmail(appUserRequestDto.getEmail());
            user.setUniqueUsername(appUserRequestDto.getUsername());
            user.setImage(imageRepository.findById(appUserRequestDto.getImageId()).orElseThrow(
                    () -> new ResourceNotFoundException("Image", "id", appUserRequestDto.getImageId())
            ));

            return userRepository.save(user);
        } else {
            throw new NotEnoughPermissionException();
        }
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
    public AppUser setUserRole(long id, SetUserRoleRequestDTO roleDto) {
        AppUser user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
        Role role = roleRepository.findById(roleDto.getRoleId()).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", roleDto.getRoleId())
        );
        user.setRole(role);
        return userRepository.save(user);
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
    public AppUser setUserImage(long id, SetUserImageRequestDTO imageDto) {
        if (
                CurrentUserUtils.hasPermissionOrIsAuthor(
                        Permission.CAN_UPDATE_OTHER_USER,
                        Permission.CAN_UPDATE_OWN_USER,
                        id,
                        userRepository
                )
        ) {

            AppUser user = userRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", id)
            );

            user.setImage(imageRepository.findById(imageDto.getImageId()).orElseThrow(
                    () -> new ResourceNotFoundException("Image", "id", imageDto.getImageId())
            ));
            return userRepository.save(user);
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
    public AppUser setUserPassword(long id, SetUserPasswordRequestDTO passwordDto) {
        if (
                CurrentUserUtils.hasPermissionOrIsAuthor(
                        Permission.CAN_UPDATE_OTHER_USER,
                        Permission.CAN_UPDATE_OWN_USER,
                        id,
                        userRepository
                )
        ) {

            AppUser user = userRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", id)
            );
            if (passwordEncoder.matches(passwordDto.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
                return userRepository.save(user);
            } else {
                throw new BadRequestException("Password");
            }
        } else {
            throw new NotEnoughPermissionException();
        }
    }
}
