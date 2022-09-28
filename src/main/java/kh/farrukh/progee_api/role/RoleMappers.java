package kh.farrukh.progee_api.role;

import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import org.springframework.beans.BeanUtils;

/**
 * It contains static methods that convert between Role, RoleRequestDTO and RoleResponseDTO
 */
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

    public static Role toRole(RoleRequestDTO roleRequestDTO) {
        if (roleRequestDTO == null) return null;
        Role role = new Role();
        BeanUtils.copyProperties(roleRequestDTO, role);
        return role;
    }
}
