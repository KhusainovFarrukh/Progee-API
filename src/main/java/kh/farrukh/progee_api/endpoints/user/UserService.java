package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

public interface UserService  {

    PagingResponse<AppUser> getUsers(
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    AppUser getUserById(Long id);

    AppUser addUser(AppUserDTO appUserDto);

    AppUser updateUser(long id, AppUserDTO appUserDto);

    void deleteUser(long id);
}
