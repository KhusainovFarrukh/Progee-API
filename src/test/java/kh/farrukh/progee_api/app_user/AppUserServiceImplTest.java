package kh.farrukh.progee_api.app_user;

import kh.farrukh.progee_api.app_user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.app_user.payloads.SetUserImageRequestDTO;
import kh.farrukh.progee_api.app_user.payloads.SetUserPasswordRequestDTO;
import kh.farrukh.progee_api.app_user.payloads.SetUserRoleRequestDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SecurityTestExecutionListeners
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AppUserServiceImpl underTest;

    @Test
    void loadUserByUsername_canLoad_whenUsernameIsValid() {
        // given
        String username = "user@gmail.com";
        when(appUserRepository.findByEmail(username)).thenReturn(Optional.of(new AppUser()));

        // when
        underTest.loadUserByUsername(username);

        // then
        verify(appUserRepository).findByEmail(username);
    }

    @Test
    void loadUserByUsername_throwsException_whenUsernameIsWrong() {
        // when
        // then
        assertThatThrownBy(() -> underTest.loadUserByUsername("user@gmail.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getUsers_canGetUsers() {
        // given
        when(appUserRepository.findAll(PageRequest.of(0, 10, Sort.Direction.ASC, "id")))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getUsers(1, 10, "id", "ASC");

        // then
        verify(appUserRepository).findAll(
                PageRequest.of(0, 10, SortUtils.parseDirection("ASC"), "id")
        );
    }

    @Test
    void getUserById_canGetUserById() {
        // given
        long userId = 1;
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser()));

        // when
        underTest.getUserById(userId);

        // then
        verify(appUserRepository).findById(userId);
    }

    @Test
    void getUserById_throwsException_whenUserDoesNotExistWithId() {
        // given
        long userId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining(String.valueOf(userId));
    }

    @Test
    void getUserByEmail_canGetUserByEmail() {
        // given
        String email = "test@mail.com";
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(new AppUser()));

        // when
        underTest.getUserByEmail(email);

        // then
        verify(appUserRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_throwsException_whenUserDoesNotExistWithEmail() {
        // given
        String email = "test@mail.com";

        // when
        // then
        assertThatThrownBy(() -> underTest.getUserByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining(email);
    }

    @Test
    void addUser_canAddUser() {
        // given
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encoded-password");
        when(imageRepository.findById(userDto.getImageId())).thenReturn(Optional.of(new Image()));
        when(roleRepository.findById(userDto.getRoleId())).thenReturn(Optional.of(new Role(Collections.emptyList())));

        // when
        underTest.addUser(userDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userArgCaptor.capture());

        AppUser actual = userArgCaptor.getValue();
        assertThat(actual.getUniqueUsername()).isEqualTo(userDto.getUniqueUsername());
        assertThat(actual.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(actual.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void addUser_throwsException_whenDuplicateEmail() {
        // given
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(appUserRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addUser(userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("email")
                .hasMessageContaining(userDto.getEmail());
    }

    @Test
    void addUser_throwsException_whenDuplicateUsername() {
        // given
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(appUserRepository.existsByUniqueUsername(userDto.getUniqueUsername())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addUser(userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("username")
                .hasMessageContaining(userDto.getUniqueUsername());
    }

    @Test
    @WithMockUser
    void updateUser_canUpdateUser_whenUserWithUpdateOwnPermission() {
        // given
        long userId = 1;
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        Role existingRole = new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, existingRole)));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(1, existingRole)));
        when(imageRepository.findById(userDto.getImageId())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateUser(userId, userDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userArgCaptor.capture());
        AppUser actual = userArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(userDto.getName());
        assertThat(actual.getUniqueUsername()).isEqualTo(userDto.getUniqueUsername());
        assertThat(actual.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @WithMockUser
    void updateUser_throwsException_whenUserWithoutUpdateOwnPermission() {
        // given
        long userId = 1;
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        Role existingRole = new Role(Collections.emptyList());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, existingRole)));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(1, existingRole)));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(userId, userDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void updateUser_canUpdateUser_whenUserWithUpdateOthersPermission() {
        // given
        long userId = 1;
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        Role existingRole = new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHER_USER));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(2, existingRole)));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(userId, existingRole)));
        when(imageRepository.findById(userDto.getImageId())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateUser(userId, userDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userArgCaptor.capture());
        AppUser actual = userArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(userDto.getName());
        assertThat(actual.getUniqueUsername()).isEqualTo(userDto.getUniqueUsername());
        assertThat(actual.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @WithMockUser
    void updateUser_throwsException_whenUserWithoutUpdateOthersPermissionUpdatesUser() {
        // given
        long userId = 1;
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        Role existingRole = new Role(Collections.emptyList());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(2, existingRole)));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(userId, existingRole)));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(userId, userDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    void updateUser_throwsException_whenUserToUpdateDoesNotExistWithId() {
        // given
        long userId = 1;
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(userId, userDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining(String.valueOf(userId));
    }

    @Test
    @WithMockUser
    void updateUser_throwsException_whenUsernameOfUserToUpdateIsAlreadyUsed() {
        // given
        long userId = 1;
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(appUserRepository.existsByUniqueUsername(userDto.getUniqueUsername())).thenReturn(true);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(userId, userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("username")
                .hasMessageContaining(userDto.getUniqueUsername());
    }

    @Test
    @WithMockUser
    void updateUser_throwsException_whenEmailOfUserToUpdateIsAlreadyUsed() {
        // given
        long userId = 1;
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(appUserRepository.existsByEmail(userDto.getEmail())).thenReturn(true);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(userId, userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("email")
                .hasMessageContaining(userDto.getEmail());
    }

    @Test
    @WithMockUser
    void updateUser_throwsException_whenImageOfUserToUpdateDoesNotExistWithId() {
        // given
        long userId = 1;
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(userId, userDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userDto.getImageId()));
    }

    @Test
    void deleteById_canDeleteUserById() {
        // given
        long userId = 1;
        when(appUserRepository.existsById(userId)).thenReturn(true);

        // when
        underTest.deleteUser(userId);

        // then
        verify(appUserRepository).deleteById(userId);
    }

    @Test
    void deleteById_throwsException_whenUserToDeleteDoesNotExistWithId() {
        // given
        long userId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining(String.valueOf(userId));
    }

    @Test
    void setUserRole_canSetUserRole() {
        // given
        SetUserRoleRequestDTO roleDto = new SetUserRoleRequestDTO(1);
        when(appUserRepository.findById(any())).thenReturn(Optional.of(new AppUser()));
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role(Collections.singletonList(Permission.CAN_VIEW_ROLE))));

        // when
        underTest.setUserRole(1, roleDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userArgCaptor.capture());
        AppUser actual = userArgCaptor.getValue();
        assertThat(Permission.CAN_VIEW_ROLE).isIn(actual.getRole().getPermissions());
    }

    @Test
    void setUserRole_throwsException_whenRoleToSetDoesNotExistWithId() {
        // given
        SetUserRoleRequestDTO roleDto = new SetUserRoleRequestDTO(1);
        when(appUserRepository.findById(any())).thenReturn(Optional.of(new AppUser()));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserRole(1, roleDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(roleDto.getRoleId()));
    }

    @Test
    void setUserRole_throwsException_whenUserToSetRoleDoesNotExistWithId() {
        // given
        long userId = 1;
        SetUserRoleRequestDTO roleDto = new SetUserRoleRequestDTO(1);

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserRole(userId, roleDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userId));
    }

    @Test
    @WithMockUser
    void setUserImage_canSetUserImage_whenUserWithUpdateOwnPermission() {
        // given
        long userId = 1;
        SetUserImageRequestDTO imageDto = new SetUserImageRequestDTO(1);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(imageRepository.findById(imageDto.getImageId())).thenReturn(Optional.of(new Image(imageDto.getImageId())));

        // when
        underTest.setUserImage(userId, imageDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userArgCaptor.capture());
        AppUser actual = userArgCaptor.getValue();
        assertThat(actual.getImage().getId()).isEqualTo(imageDto.getImageId());
    }

    @Test
    @WithMockUser
    void setUserImage_throwsException_whenUserWithoutUpdateOwnPermission() {
        // given
        long userId = 1;
        SetUserImageRequestDTO imageDto = new SetUserImageRequestDTO(1);
        AppUser user = new AppUser(userId, new Role(Collections.emptyList()));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(userId, imageDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void setUserImage_canSetUserImage_whenUserWithUpdateOtherPermission() {
        // given
        long userId = 1;
        SetUserImageRequestDTO imageDto = new SetUserImageRequestDTO(1);
        AppUser user = new AppUser(2, new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHER_USER)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(userId)));
        when(imageRepository.findById(imageDto.getImageId())).thenReturn(Optional.of(new Image(imageDto.getImageId())));

        // when
        underTest.setUserImage(1, imageDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userArgCaptor.capture());
        AppUser actual = userArgCaptor.getValue();
        assertThat(actual.getImage().getId()).isEqualTo(imageDto.getImageId());
    }

    @Test
    @WithMockUser
    void setUserImage_throwsException_whenUserWithoutUpdateOtherPermission() {
        // given
        long userId = 1;
        SetUserImageRequestDTO imageDto = new SetUserImageRequestDTO(1);
        AppUser user = new AppUser(2, new Role(Collections.emptyList()));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(userId, imageDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void setUserImage_throwsException_whenUserToSetImageDoesNotExistWithId() {
        // given
        long userId = 1;
        SetUserImageRequestDTO imageDto = new SetUserImageRequestDTO(1);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(userId, imageDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userId));
    }

    @Test
    @WithMockUser
    void setUserImage_throwsException_when_ImageToSetDoesNotExistWithId() {
        // given
        long userId = 1;
        SetUserImageRequestDTO imageDto = new SetUserImageRequestDTO(1);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(userId, imageDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(imageDto.getImageId()));
    }

    @Test
    @WithMockUser
    void setUserPassword_canSetPassword_whenUserWithUpdateOwnPermission() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        SetUserPasswordRequestDTO passwordDto = new SetUserPasswordRequestDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        // when
        underTest.setUserPassword(userId, passwordDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userArgCaptor.capture());
        AppUser actual = userArgCaptor.getValue();
        assertThat(actual.getPassword()).isEqualTo(passwordDto.getNewPassword());
    }

    @Test
    @WithMockUser
    void setUserPassword_throwsException_whenUserWithoutUpdateOwnPermission() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        SetUserPasswordRequestDTO passwordDto = new SetUserPasswordRequestDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId, new Role(Collections.emptyList()));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(userId, passwordDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void setUserPassword_canSetPassword_whenUserWithUpdateOtherPermission() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        SetUserPasswordRequestDTO passwordDto = new SetUserPasswordRequestDTO(currentPassword, newPassword);
        AppUser user = new AppUser(2, new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHER_USER)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(new AppUser(userId)));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        // when
        underTest.setUserPassword(userId, passwordDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userArgCaptor.capture());
        AppUser actual = userArgCaptor.getValue();
        assertThat(actual.getPassword()).isEqualTo(passwordDto.getNewPassword());
    }

    @Test
    @WithMockUser
    void setUserPassword_throwsException_whenUserWithoutUpdateOtherPermission() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        SetUserPasswordRequestDTO passwordDto = new SetUserPasswordRequestDTO(currentPassword, newPassword);
        AppUser user = new AppUser(2, new Role(Collections.emptyList()));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(userId, passwordDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void setUserPassword_throwsException_whenCurrentPasswordIsWrong() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        SetUserPasswordRequestDTO passwordDto = new SetUserPasswordRequestDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(userId, passwordDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Password");
    }

    @Test
    @WithMockUser
    void setUserPassword_throwsException_whenUserToSetPasswordDoesNotExistWithId() {
        // given
        long userId = 1;
        SetUserPasswordRequestDTO passwordDto = new SetUserPasswordRequestDTO("", "");
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(userId, passwordDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userId));
    }
}