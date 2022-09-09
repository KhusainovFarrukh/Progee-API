package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.user.payloads.*;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * A base interface for service of User endpoints
 * <p>
 * Methods implemented in UserServiceImpl
 */
public interface AppUserService extends UserDetailsService {

    PagingResponse<AppUserResponseDTO> getUsers(
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    AppUserResponseDTO getUserById(Long id);

    AppUserResponseDTO getUserByEmail(String email);

    AppUserResponseDTO addUser(AppUserRequestDTO appUserRequestDto);

    AppUserResponseDTO updateUser(long id, AppUserRequestDTO appUserRequestDto);

    void deleteUser(long id);

    AppUserResponseDTO setUserRole(long id, SetUserRoleRequestDTO roleDto);

    AppUserResponseDTO setUserImage(long id, SetUserImageRequestDTO imageDto);

    AppUserResponseDTO setUserPassword(long id, SetUserPasswordRequestDTO passwordDto);
}
