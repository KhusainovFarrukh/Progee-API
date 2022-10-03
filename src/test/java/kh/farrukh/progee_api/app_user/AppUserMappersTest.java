package kh.farrukh.progee_api.app_user;

import kh.farrukh.progee_api.app_user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.app_user.payloads.AppUserResponseDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserMappersTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ImageRepository imageRepository;

    @Test
    void toAppUserResponseDTO_canMap_whenAppUserIsValid() {
        // given
        AppUser appUser = new AppUser(
                "Test",
                "test@mail.com",
                "test",
                "not-secure",
                true,
                false,
                new Role(),
                new Image(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        // when
        AppUserResponseDTO actual = AppUserMappers.toAppUserResponseDTO(appUser);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(appUser.getName());
        assertThat(actual.getEmail()).isEqualTo(appUser.getEmail());
        assertThat(actual.getUniqueUsername()).isEqualTo(appUser.getUniqueUsername());
        assertThat(actual.isEnabled()).isEqualTo(appUser.isEnabled());
        assertThat(actual.isLocked()).isEqualTo(appUser.isLocked());
        assertThat(actual.getRole().getId()).isEqualTo(appUser.getRole().getId());
        assertThat(actual.getImage().getId()).isEqualTo(appUser.getImage().getId());
    }

    @Test
    void toAppUserResponseDTO_returnsNull_whenAppUserIsNull() {
        // given
        AppUser appUser = null;

        // when
        AppUserResponseDTO actual = AppUserMappers.toAppUserResponseDTO(appUser);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toAppUser_canMap_whenAppUserResponseDTOIsValid() {
        // given
        AppUserResponseDTO appUserResponseDTO = new AppUserResponseDTO(
                1L,
                "Test",
                "test@mail.com",
                "test",
                true,
                false,
                new RoleResponseDTO(),
                new ImageResponseDTO()
        );

        // when
        AppUser actual = AppUserMappers.toAppUser(appUserResponseDTO);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(appUserResponseDTO.getName());
        assertThat(actual.getEmail()).isEqualTo(appUserResponseDTO.getEmail());
        assertThat(actual.getUniqueUsername()).isEqualTo(appUserResponseDTO.getUniqueUsername());
        assertThat(actual.isEnabled()).isEqualTo(appUserResponseDTO.isEnabled());
        assertThat(actual.isLocked()).isEqualTo(appUserResponseDTO.isLocked());
        assertThat(actual.getRole().getId()).isEqualTo(appUserResponseDTO.getRole().getId());
        assertThat(actual.getImage().getId()).isEqualTo(appUserResponseDTO.getImage().getId());
    }

    @Test
    void toAppUser_returnsNull_whenAppUserResponseDTOIsNull() {
        // given
        AppUserResponseDTO appUserResponseDTO = null;

        // when
        AppUser actual = AppUserMappers.toAppUser(appUserResponseDTO);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toAppUser_canMap_whenAppUserRequestDTOIsValid() {
        // given
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role(1L)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(2L)));
        AppUserRequestDTO appUserRequestDTO = new AppUserRequestDTO(
                "Test",
                "test@mail.com",
                "test",
                "not-secure",
                true,
                false,
                1L,
                2L
        );

        // when
        AppUser actual = AppUserMappers.toAppUser(appUserRequestDTO, roleRepository, imageRepository);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(appUserRequestDTO.getName());
        assertThat(actual.getEmail()).isEqualTo(appUserRequestDTO.getEmail());
        assertThat(actual.getUniqueUsername()).isEqualTo(appUserRequestDTO.getUniqueUsername());
        assertThat(actual.isEnabled()).isEqualTo(appUserRequestDTO.isEnabled());
        assertThat(actual.isLocked()).isEqualTo(appUserRequestDTO.isLocked());
        assertThat(actual.getRole().getId()).isEqualTo(appUserRequestDTO.getRoleId());
        assertThat(actual.getImage().getId()).isEqualTo(appUserRequestDTO.getImageId());
    }

    @Test
    void toAppUser_returnsNull_whenAppUserRequestDTOIsNull() {
        // given
        AppUserRequestDTO appUserRequestDTO = null;

        // when
        AppUser actual = AppUserMappers.toAppUser(appUserRequestDTO, roleRepository, imageRepository);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toAppUser_throwsException_whenRoleDoesNotExistWithId() {
        // given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        when(imageRepository.findById(2L)).thenReturn(Optional.of(new Image(2L)));
        AppUserRequestDTO appUserRequestDTO = new AppUserRequestDTO(
                "Test",
                "test@mail.com",
                "test",
                "not-secure",
                true,
                false,
                1L,
                2L
        );

        // then
        assertThatThrownBy(() -> AppUserMappers.toAppUser(appUserRequestDTO, roleRepository, imageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role")
                .hasMessageContaining("1");
    }

    @Test
    void toAppUser_throwsException_whenImageDoesNotExistWithId() {
        // given
        when(imageRepository.findById(2L)).thenReturn(Optional.empty());
        AppUserRequestDTO appUserRequestDTO = new AppUserRequestDTO(
                "Test",
                "test@mail.com",
                "test",
                "not-secure",
                true,
                false,
                1L,
                2L
        );

        // then
        assertThatThrownBy(() -> AppUserMappers.toAppUser(appUserRequestDTO, roleRepository, imageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("2");
    }
}