package kh.farrukh.progee_api.endpoints.role;

import kh.farrukh.progee_api.endpoints.role.payloads.RoleResponseDTO;
import org.springframework.beans.BeanUtils;

public class RoleMappers {

    public static RoleResponseDTO toRoleResponseDTO(Role role) {
        if (role == null) return null;
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();
        BeanUtils.copyProperties(role, roleResponseDTO);
        return roleResponseDTO;
    }

    public static Role toRole(RoleResponseDTO roleResponseDTO) {
        if (roleResponseDTO == null) return null;
        Role role = new Role();
        BeanUtils.copyProperties(roleResponseDTO, role);
        return role;
    }
}
