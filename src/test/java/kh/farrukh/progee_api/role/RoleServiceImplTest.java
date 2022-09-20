package kh.farrukh.progee_api.role;


import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl underTest;

    @Test
    void canGetAllRoles() {
        // given
        when(roleRepository.findAll(any(Pageable.class))).thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getRoles(1, 10);

        // then
        verify(roleRepository).findAll(PageRequest.of(
                0,
                10
        ));
    }

    @Test
    void canGetRoleById() {
        // given
        long id = 1L;
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role()));

        // when
        underTest.getRoleById(id);

        // then
        verify(roleRepository).findById(id);
    }

    @Test
    void canAddRole() {
        // given
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO("User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE));
        when(roleRepository.save(any())).thenReturn(new Role());

        // when
        underTest.addRole(roleRequestDTO);

        // then
        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleArgumentCaptor.capture());

        Role capturedRole = roleArgumentCaptor.getValue();
        assertThat(capturedRole.getTitle()).isEqualTo(roleRequestDTO.getTitle());
        assertThat(capturedRole.getPermissions()).isEqualTo(roleRequestDTO.getPermissions());
        assertThat(capturedRole.isDefault()).isEqualTo(roleRequestDTO.isDefault());
    }

    @Test
    void canUpdateRole() {
        // given
        long id = 1L;
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO(
                "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)
        );
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role()));
        when(roleRepository.save(any())).thenReturn(new Role());

        // when
        underTest.updateRole(id, roleRequestDTO);

        // then
        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleArgumentCaptor.capture());

        Role capturedRole = roleArgumentCaptor.getValue();
        assertThat(capturedRole.getTitle()).isEqualTo(roleRequestDTO.getTitle());
        assertThat(capturedRole.getPermissions()).isEqualTo(roleRequestDTO.getPermissions());
        assertThat(capturedRole.isDefault()).isEqualTo(roleRequestDTO.isDefault());
    }

    @Test
    void throwsExceptionIfRoleWithTitleExists() {
        // given
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO(
                "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)
        );
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role()));
        when(roleRepository.existsByTitle("User")).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.updateRole(1, roleRequestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Role")
                .hasMessageContaining(roleRequestDTO.getTitle());
    }

    @Test
    void throwsExceptionIfRoleToUpdateDoesNotExist() {
        // given
        long id = 1L;
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO(
                "User", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)
        );

        // when
        // then
        assertThatThrownBy(() -> underTest.updateRole(id, roleRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    void canDeleteRole() {
        // given
        long id = 1L;
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role()));
        when(roleRepository.findFirstByIsDefaultIsTrueAndIdNot(any())).thenReturn(Optional.of(new Role()));

        // when
        underTest.deleteRoleById(id);

        // then
        verify(roleRepository).deleteById(id);
    }

    @Test
    void throwsExceptionIfRoleToDeleteDoesNotExist() {
        // given
        long id = 1L;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteRoleById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role")
                .hasMessageContaining(String.valueOf(id));
    }

}