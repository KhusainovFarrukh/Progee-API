package kh.farrukh.progee_api.app_user;

import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.global.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.global.utils.user.CurrentUserUtils;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.app_user.payloads.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static kh.farrukh.progee_api.global.utils.checkers.Checkers.*;

/**
 * It implements the UserService interface and the UserDetailsService interface,
 * uses the UserRepository, PasswordEncoder and ImageRepository to perform CRUD operations on the AppUser entity
 */
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
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
        return appUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Wrong email"));
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
    public PagingResponse<AppUserResponseDTO> getUsers(
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        return new PagingResponse<>(appUserRepository.findAll(
                PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
        ).map(AppUserMappers::toAppUserResponseDTO));
    }

    /**
     * If the user exists, return the user, otherwise throw an exception.
     *
     * @param id The id of the user to retrieve
     * @return The AppUserResponseDTO is being returned.
     */
    @Override
    public AppUserResponseDTO getUserById(Long id) {
        return appUserRepository.findById(id)
                .map(AppUserMappers::toAppUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * If the user exists, return the user, otherwise throw an exception.
     *
     * @param email The email of the user to retrieve
     * @return The AppUserResponseDTO is being returned.
     */
    @Override
    public AppUserResponseDTO getUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .map(AppUserMappers::toAppUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Creates a new AppUser object, encodes the password, and saves the user
     *
     * @param appUserRequestDto The DTO object that contains the user's information.
     * @return The created user.
     */
    @Override
    public AppUserResponseDTO addUser(AppUserRequestDTO appUserRequestDto) {
        checkUserIsUnique(appUserRepository, appUserRequestDto);
        AppUser appUser = AppUserMappers.toAppUser(appUserRequestDto, roleRepository, imageRepository);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return AppUserMappers.toAppUserResponseDTO(appUserRepository.save(appUser));
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
    public AppUserResponseDTO updateUser(long id, AppUserRequestDTO appUserRequestDto) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (CurrentUserUtils.hasPermissionOrIsAuthor(
                Permission.CAN_UPDATE_OTHER_USER,
                Permission.CAN_UPDATE_OWN_USER,
                user.getId(),
                appUserRepository)) {

            // It checks if the username of the user is changed and if the new username is already taken.
            if (!appUserRequestDto.getUniqueUsername().equals(user.getUniqueUsername()) &&
                    appUserRepository.existsByUniqueUsername(appUserRequestDto.getUniqueUsername())) {
                throw new DuplicateResourceException("User", "username", appUserRequestDto.getUniqueUsername());
            }

            // It checks if the email of the user is changed and if the new email is already taken.
            if (!appUserRequestDto.getEmail().equals(user.getEmail()) &&
                    appUserRepository.existsByEmail(appUserRequestDto.getEmail())) {
                throw new DuplicateResourceException("User", "email", appUserRequestDto.getEmail());
            }

            user.setName(appUserRequestDto.getName());
            user.setEmail(appUserRequestDto.getEmail());
            user.setUniqueUsername(appUserRequestDto.getUniqueUsername());
            user.setImage(imageRepository.findById(appUserRequestDto.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", appUserRequestDto.getImageId())));

            return AppUserMappers.toAppUserResponseDTO(appUserRepository.save(user));
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
        checkUserId(appUserRepository, id);
        appUserRepository.deleteById(id);
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
    public AppUserResponseDTO setUserRole(long id, SetUserRoleRequestDTO roleDto) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        Role role = roleRepository.findById(roleDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleDto.getRoleId()));
        user.setRole(role);
        return AppUserMappers.toAppUserResponseDTO(appUserRepository.save(user));
    }

    /**
     * It takes in a user id and a SetUserImageRequestDTO object, finds the user in the database,
     * sets the user's image to the image with id in the SetUserImageRequestDTO object, and returns the user
     *
     * @param id       The id of the user to be updated
     * @param imageDto This is the object that will be passed in the request body.
     * @return The updated user.
     */
    @Override
    public AppUserResponseDTO setUserImage(long id, SetUserImageRequestDTO imageDto) {
        if (CurrentUserUtils.hasPermissionOrIsAuthor(
                Permission.CAN_UPDATE_OTHER_USER,
                Permission.CAN_UPDATE_OWN_USER,
                id,
                appUserRepository)
        ) {
            AppUser user = appUserRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            user.setImage(imageRepository.findById(imageDto.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageDto.getImageId())));

            return AppUserMappers.toAppUserResponseDTO(appUserRepository.save(user));
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * It takes in a user id and a SetUserPasswordRequestDTO object, finds the user in the database,
     * checks the current password, sets the user's password to the password in the SetUserPasswordRequestDTO object,
     * and returns the user
     *
     * @param id          The id of the user to be updated
     * @param passwordDto This is the object that will be passed in the request body.
     * @return The updated user.
     */
    @Override
    public AppUserResponseDTO setUserPassword(long id, SetUserPasswordRequestDTO passwordDto) {
        if (CurrentUserUtils.hasPermissionOrIsAuthor(
                Permission.CAN_UPDATE_OTHER_USER,
                Permission.CAN_UPDATE_OWN_USER,
                id,
                appUserRepository)
        ) {

            AppUser user = appUserRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            if (passwordEncoder.matches(passwordDto.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
                return AppUserMappers.toAppUserResponseDTO(appUserRepository.save(user));
            } else {
                throw new BadRequestException("Password");
            }
        } else {
            throw new NotEnoughPermissionException();
        }
    }
}
