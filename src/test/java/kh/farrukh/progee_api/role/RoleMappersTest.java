package kh.farrukh.progee_api.role;

import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoleMappersTest {

    @Test
    void toRoleResponseDTO_returnsNull_whenRoleIsNull() {
        // given
        Role role = null;

        // when
        RoleResponseDTO actual = RoleMappers.toRoleResponseDTO(role);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toRoleResponseDTO_canMap_whenRoleIsValid() {
        // given
        Role role = new Role(1L, "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE));

        // when
        RoleResponseDTO actual = RoleMappers.toRoleResponseDTO(role);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getTitle()).isEqualTo(role.getTitle());
        assertThat(actual.isDefault()).isEqualTo(role.isDefault());
        assertThat(actual.getPermissions()).isEqualTo(role.getPermissions());
    }

    @Test
    void toRole_returnsNull_whenRoleResponseDTOIsNull() {
        // given
        RoleResponseDTO roleResponseDTO = null;

        // when
        Role actual = RoleMappers.toRole(roleResponseDTO);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toRole_canMap_whenRoleResponseDTOIsValid() {
        // given
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO(
                1L, "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)
        );

        // when
        Role actual = RoleMappers.toRole(roleResponseDTO);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getTitle()).isEqualTo(roleResponseDTO.getTitle());
        assertThat(actual.isDefault()).isEqualTo(roleResponseDTO.isDefault());
        assertThat(actual.getPermissions()).isEqualTo(roleResponseDTO.getPermissions());
    }

    @Test
    void toRole_returnsNull_whenRoleRequestDTOIsNull() {
        // given
        RoleRequestDTO roleRequestDTO = null;

        // when
        Role actual = RoleMappers.toRole(roleRequestDTO);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toRole_canMap_whenRoleRequestDTOisValid() {
        // given
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO(
                "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)
        );

        // when
        Role actual = RoleMappers.toRole(roleRequestDTO);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getTitle()).isEqualTo(roleRequestDTO.getTitle());
        assertThat(actual.isDefault()).isEqualTo(roleRequestDTO.isDefault());
        assertThat(actual.getPermissions()).isEqualTo(roleRequestDTO.getPermissions());
    }
}