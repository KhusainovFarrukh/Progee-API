package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.endpoints.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

public interface RoleService {

    PagingResponse<Role> getRoles(int pageNumber, int pageSize);

    Role getRoleById(long id);

    Role addRole(RoleRequestDTO roleRequestDTO);

    Role updateRole(long id, RoleRequestDTO roleRequestDTO);

    void deleteRoleById(long id);
}