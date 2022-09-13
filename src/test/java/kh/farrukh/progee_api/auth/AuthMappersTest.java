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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AuthMappersTest {

    @Mock
    private RoleRepository roleRepository;

    @Test
    void canMapRegistrationRequestDTOToAppUserRequestDTO() {
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
        AppUserRequestDTO appUserRequestDTO = AuthMappers.toAppUserRequestDTO(registrationRequestDTO, roleRepository);

        // then
        assertThat(appUserRequestDTO).isNotNull();
        assertThat(appUserRequestDTO.getName()).isEqualTo(registrationRequestDTO.getName());
        assertThat(appUserRequestDTO.getUniqueUsername()).isEqualTo(registrationRequestDTO.getUniqueUsername());
        assertThat(appUserRequestDTO.getEmail()).isEqualTo(registrationRequestDTO.getEmail());
        assertThat(appUserRequestDTO.getPassword()).isEqualTo(registrationRequestDTO.getPassword());
        assertThat(appUserRequestDTO.getImageId()).isEqualTo(registrationRequestDTO.getImageId());
        assertThat(appUserRequestDTO.isEnabled()).isTrue();
        assertThat(appUserRequestDTO.isLocked()).isFalse();
        assertThat(appUserRequestDTO.getRoleId()).isEqualTo(1);
    }

    @Test
    void returnsNullIfRegistrationRequestDTOIsNull() {
        // given
        RegistrationRequestDTO registrationRequestDTO = null;

        // when
        AppUserRequestDTO appUserRequestDTO = AuthMappers.toAppUserRequestDTO(registrationRequestDTO, roleRepository);

        // then
        assertThat(appUserRequestDTO).isNull();
    }

    @Test
    void throwsExceptionIfDefaultRoleDoesNotExist() {
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