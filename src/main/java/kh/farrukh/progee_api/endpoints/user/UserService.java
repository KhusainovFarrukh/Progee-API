package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.endpoints.user.payloads.SetUserImageRequestDTO;
import kh.farrukh.progee_api.endpoints.user.payloads.SetUserPasswordRequestDTO;
import kh.farrukh.progee_api.endpoints.user.payloads.SetUserRoleRequestDTO;
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

    AppUser addUser(AppUserRequestDTO appUserRequestDto);

    AppUser updateUser(long id, AppUserRequestDTO appUserRequestDto);

    void deleteUser(long id);

    AppUser setUserRole(long id, SetUserRoleRequestDTO roleDto);

    AppUser setUserImage(long id, SetUserImageRequestDTO imageDto);

    AppUser setUserPassword(long id, SetUserPasswordRequestDTO passwordDto);
}
