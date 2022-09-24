package kh.farrukh.progee_api.app_user;

import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.app_user.payloads.*;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * A base interface for service of User endpoints
 * <p>
 * Methods implemented in UserServiceImpl
 * <p>
 * Implements UserDetailsService to be used in Spring Security.
 * It means that this class is injected as bean dependency to Spring Security Configurations
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
