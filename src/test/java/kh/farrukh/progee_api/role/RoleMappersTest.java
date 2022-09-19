package kh.farrukh.progee_api.role;

import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoleMappersTest {

    @Test
    void returnsNullIfRoleIsNull() {
        // given
        Role role = null;

        // when
        RoleResponseDTO roleResponseDTO = RoleMappers.toRoleResponseDTO(role);

        // then
        assertThat(roleResponseDTO).isNull();
    }

    @Test
    void canMapRoleToRoleResponseDTO() {
        // given
        Role role = new Role(1L, "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE));

        // when
        RoleResponseDTO roleResponseDTO = RoleMappers.toRoleResponseDTO(role);

        // then
        assertThat(roleResponseDTO).isNotNull();
        assertThat(roleResponseDTO.getId()).isEqualTo(1L);
        assertThat(roleResponseDTO.getTitle()).isEqualTo(role.getTitle());
        assertThat(roleResponseDTO.isDefault()).isEqualTo(role.isDefault());
        assertThat(roleResponseDTO.getPermissions()).isEqualTo(role.getPermissions());
    }

    @Test
    void returnsNullIfRoleResponseDTOIsNull() {
        // given
        RoleResponseDTO roleResponseDTO = null;

        // when
        Role role = RoleMappers.toRole(roleResponseDTO);

        // then
        assertThat(role).isNull();
    }

    @Test
    void canMapRoleResponseDTOToRole() {
        // given
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO(
                1L, "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)
        );

        // when
        Role role = RoleMappers.toRole(roleResponseDTO);

        // then
        assertThat(role).isNotNull();
        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getTitle()).isEqualTo(roleResponseDTO.getTitle());
        assertThat(role.isDefault()).isEqualTo(roleResponseDTO.isDefault());
        assertThat(role.getPermissions()).isEqualTo(roleResponseDTO.getPermissions());
    }

    @Test
    void returnsNullIfRoleRequestDTOIsNull() {
        // given
        RoleRequestDTO roleRequestDTO = null;

        // when
        Role role = RoleMappers.toRole(roleRequestDTO);

        // then
        assertThat(role).isNull();
    }

    @Test
    void canMapRoleRequestDTOToRole() {
        // given
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO(
                "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)
        );

        // when
        Role role = RoleMappers.toRole(roleRequestDTO);

        // then
        assertThat(role).isNotNull();
        assertThat(role.getTitle()).isEqualTo(roleRequestDTO.getTitle());
        assertThat(role.isDefault()).isEqualTo(roleRequestDTO.isDefault());
        assertThat(role.getPermissions()).isEqualTo(roleRequestDTO.getPermissions());
    }
}