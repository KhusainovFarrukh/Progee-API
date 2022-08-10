package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * A base interface for service of User endpoints
 * <p>
 * Methods implemented in UserServiceImpl
 */
public interface UserService extends UserDetailsService {

    PagingResponse<AppUser> getUsers(
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    AppUser getUserById(Long id);

    AppUser getUserByEmail(String email);

    AppUser addUser(AppUserDTO appUserDto);

    AppUser updateUser(long id, AppUserDTO appUserDto);

    void deleteUser(long id);

    AppUser setUserRole(long id, UserRoleDTO roleDto);

    AppUser setUserImage(long id, UserImageDTO imageDto);

    AppUser setUserPassword(long id, UserPasswordDTO passwordDto);
}
