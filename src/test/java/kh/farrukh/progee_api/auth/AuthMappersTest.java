package kh.farrukh.progee_api.auth;

import kh.farrukh.progee_api.app_user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthMappersTest {

    @Mock
    private RoleRepository roleRepository;

    @Test
    void toAppUserRequestDTO_canMap_whenRegistrationRequestDTOIsValid() {
        // given
        when(roleRepository.findFirstByIsDefaultIsTrue()).thenReturn(Optional.of(new Role(1)));
        RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO(
                "Test",
                "test",
                "test@mail.com",
                "not-secured",
                1L
        );

        // when
        AppUserRequestDTO actual = AuthMappers.toAppUserRequestDTO(registrationRequestDTO, roleRepository);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(registrationRequestDTO.getName());
        assertThat(actual.getUniqueUsername()).isEqualTo(registrationRequestDTO.getUniqueUsername());
        assertThat(actual.getEmail()).isEqualTo(registrationRequestDTO.getEmail());
        assertThat(actual.getPassword()).isEqualTo(registrationRequestDTO.getPassword());
        assertThat(actual.getImageId()).isEqualTo(registrationRequestDTO.getImageId());
        assertThat(actual.isEnabled()).isTrue();
        assertThat(actual.isLocked()).isFalse();
        assertThat(actual.getRoleId()).isEqualTo(1);
    }

    @Test
    void toAppUserRequestDTO_returnsNull_whenRegistrationRequestDTOIsNull() {
        // given
        RegistrationRequestDTO registrationRequestDTO = null;

        // when
        AppUserRequestDTO actual = AuthMappers.toAppUserRequestDTO(registrationRequestDTO, roleRepository);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toAppUserRequestDTO_throwsException_whenDefaultRoleDoesNotExist() {
        // given
        when(roleRepository.findFirstByIsDefaultIsTrue()).thenReturn(Optional.empty());
        RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO(
                "Test",
                "test",
                "test@mail.com",
                "not-secured",
                1L
        );

        // when
        // then
        assertThatThrownBy(() -> AuthMappers.toAppUserRequestDTO(registrationRequestDTO, roleRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role")
                .hasMessageContaining("isDefault")
                .hasMessageContaining("true");
    }
}