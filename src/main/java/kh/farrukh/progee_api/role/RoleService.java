package kh.farrukh.progee_api.role;

import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;

/**
 * A base interface for service of Role endpoints
 * Methods implemented in RoleServiceImpl
 */
public interface RoleService {

    PagingResponse<RoleResponseDTO> getRoles(int pageNumber, int pageSize);

    RoleResponseDTO getRoleById(long id);

    RoleResponseDTO addRole(RoleRequestDTO roleRequestDTO);

    RoleResponseDTO updateRole(long id, RoleRequestDTO roleRequestDTO);

    void deleteRoleById(long id);
}