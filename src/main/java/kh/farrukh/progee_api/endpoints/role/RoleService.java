package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

public interface RoleService {

    PagingResponse<Role> getRoles(int pageNumber, int pageSize);

    Role getRoleById(long id);

    Role addRole(RoleDTO roleDTO);

    Role updateRole(long id, RoleDTO roleDTO);

    void deleteRoleById(long id);
}