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
        AppUserResponseDTO appUserResponseDTO = AppUserMappers.toAppUserResponseDTO(appUser);

        // then
        assertThat(appUserResponseDTO).isNotNull();
        assertThat(appUserResponseDTO.getName()).isEqualTo(appUser.getName());
        assertThat(appUserResponseDTO.getEmail()).isEqualTo(appUser.getEmail());
        assertThat(appUserResponseDTO.getUniqueUsername()).isEqualTo(appUser.getUniqueUsername());
        assertThat(appUserResponseDTO.isEnabled()).isEqualTo(appUser.isEnabled());
        assertThat(appUserResponseDTO.isLocked()).isEqualTo(appUser.isLocked());
        assertThat(appUserResponseDTO.getRole().getId()).isEqualTo(appUser.getRole().getId());
        assertThat(appUserResponseDTO.getImage().getId()).isEqualTo(appUser.getImage().getId());
    }

    @Test
    void toAppUserResponseDTO_returnsNull_whenAppUserIsNull() {
        // given
        AppUser appUser = null;

        // when
        AppUserResponseDTO appUserResponseDTO = AppUserMappers.toAppUserResponseDTO(appUser);

        // then
        assertThat(appUserResponseDTO).isNull();
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
        AppUser appUser = AppUserMappers.toAppUser(appUserResponseDTO);

        // then
        assertThat(appUser).isNotNull();
        assertThat(appUser.getName()).isEqualTo(appUserResponseDTO.getName());
        assertThat(appUser.getEmail()).isEqualTo(appUserResponseDTO.getEmail());
        assertThat(appUser.getUniqueUsername()).isEqualTo(appUserResponseDTO.getUniqueUsername());
        assertThat(appUser.isEnabled()).isEqualTo(appUserResponseDTO.isEnabled());
        assertThat(appUser.isLocked()).isEqualTo(appUserResponseDTO.isLocked());
        assertThat(appUser.getRole().getId()).isEqualTo(appUserResponseDTO.getRole().getId());
        assertThat(appUser.getImage().getId()).isEqualTo(appUserResponseDTO.getImage().getId());
    }

    @Test
    void toAppUser_returnsNull_whenAppUserResponseDTOIsNull() {
        // given
        AppUserResponseDTO appUserResponseDTO = null;

        // when
        AppUser appUser = AppUserMappers.toAppUser(appUserResponseDTO);

        // then
        assertThat(appUser).isNull();
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
        AppUser appUser = AppUserMappers.toAppUser(appUserRequestDTO, roleRepository, imageRepository);

        // then
        assertThat(appUser).isNotNull();
        assertThat(appUser.getName()).isEqualTo(appUserRequestDTO.getName());
        assertThat(appUser.getEmail()).isEqualTo(appUserRequestDTO.getEmail());
        assertThat(appUser.getUniqueUsername()).isEqualTo(appUserRequestDTO.getUniqueUsername());
        assertThat(appUser.isEnabled()).isEqualTo(appUserRequestDTO.isEnabled());
        assertThat(appUser.isLocked()).isEqualTo(appUserRequestDTO.isLocked());
        assertThat(appUser.getRole().getId()).isEqualTo(appUserRequestDTO.getRoleId());
        assertThat(appUser.getImage().getId()).isEqualTo(appUserRequestDTO.getImageId());
    }

    @Test
    void toAppUser_returnsNull_whenAppUserRequestDTOIsNull() {
        // given
        AppUserRequestDTO appUserRequestDTO = null;

        // when
        AppUser appUser = AppUserMappers.toAppUser(appUserRequestDTO, roleRepository, imageRepository);

        // then
        assertThat(appUser).isNull();
    }

    @Test
    void toAppUser_throwsException_whenRoleDoesNotExistWithId() {
        // given
        when(roleRepository.findById(any())).thenReturn(Optional.empty());
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

        // then
        assertThatThrownBy(() -> AppUserMappers.toAppUser(appUserRequestDTO, roleRepository, imageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role")
                .hasMessageContaining("1");
    }

    @Test
    void toAppUser_throwsException_whenImageDoesNotExistWithId() {
        // given
        when(imageRepository.findById(any())).thenReturn(Optional.empty());
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