package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SecurityTestExecutionListeners
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl underTest;

    @Test
    void canLoadUserByUsername() {
        // given
        String username = "user@gmail.com";
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser()));

        // when
        underTest.loadUserByUsername(username);

        // then
        verify(userRepository).findByEmail(username);
    }

    @Test
    void throwsExceptionIfWrongUsername() {
        // given
        String username = "user@gmail.com";

        // when
        // then
        assertThatThrownBy(() -> underTest.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void canGetUsers() {
        // when
        underTest.getUsers(1, 10, "id", "ASC");

        // then
        verify(userRepository).findAll(
                PageRequest.of(0, 10, SortUtils.parseDirection("ASC"), "id")
        );

    }

    @Test
    void canGetUserById() {
        // given
        long userId = 1;
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser()));

        // when
        underTest.getUserById(userId);

        // then
        verify(userRepository).findById(userId);
    }

    @Test
    void throwsExceptionIfUserDoesNotExistWithId() {
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
    void canAddUser() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role(Collections.emptyList())));

        // when
        underTest.addUser(userDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userArgCaptor.capture());

        AppUser capturedUser = userArgCaptor.getValue();
        assertThat(capturedUser.getUniqueUsername()).isEqualTo(userDto.getUsername());
        assertThat(capturedUser.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(capturedUser.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void throwsExceptionIfDuplicateEmail() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addUser(userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("email");
    }

    @Test
    void throwsExceptionIfDuplicateUsername() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(userRepository.existsByUniqueUsername(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addUser(userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("username");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithUpdateOwnPermissionCanUpdateUser() {
        // given
        AppUserDTO userDto = new AppUserDTO(
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
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, existingRole)));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(1, existingRole)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        AppUser actual = underTest.updateUser(1, userDto);

        // then
        assertThat(actual.getName()).isEqualTo(userDto.getName());
        assertThat(actual.getUniqueUsername()).isEqualTo(userDto.getUsername());
        assertThat(actual.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserWithoutUpdateOwnPermissionUpdatesUser() {
        // given
        AppUserDTO userDto = new AppUserDTO(
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
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, existingRole)));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(1, existingRole)));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(1, userDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithUpdateOthersPermissionCanUpdateUser() {
        // given
        AppUserDTO userDto = new AppUserDTO(
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
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, existingRole)));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(2, existingRole)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        AppUser actual = underTest.updateUser(2, userDto);

        // then
        assertThat(actual.getName()).isEqualTo(userDto.getName());
        assertThat(actual.getUniqueUsername()).isEqualTo(userDto.getUsername());
        assertThat(actual.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserWithoutUpdateOthersPermissionUpdatesUser() {
        // given
        AppUserDTO userDto = new AppUserDTO(
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
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, existingRole)));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(2, existingRole)));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(2, userDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    void throwsExceptionIfUserToUpdateDoesNotExistWithId() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(1, userDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUsernameOfUserToUpdateIsAlreadyUsed() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(userRepository.existsByUniqueUsername(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(1, userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("username");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfEmailOfUserToUpdateIsAlreadyUsed() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(1, userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("email");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfImageOfUserToUpdateDoesNotExistWithId() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                1,
                1
        );
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)))));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(1, userDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("id");
    }

    @Test
    void canDeleteUserById() {
        // given
        long userId = 1;
        when(userRepository.existsById(any())).thenReturn(true);

        // when
        underTest.deleteUser(userId);

        // then
        verify(userRepository).deleteById(userId);
    }

    @Test
    void throwsExceptionIfUserToDeleteDoesNotExistWithId() {
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
    void canSetUserRole() {
        // given
        UserRoleDTO roleDto = new UserRoleDTO(1);
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser()));
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role(Collections.singletonList(Permission.CAN_VIEW_ROLE))));

        // when
        AppUser actual = underTest.setUserRole(1, roleDto);

        // then
        assertThat(Permission.CAN_VIEW_ROLE).isIn(actual.getRole().getPermissions());
    }

    @Test
    void throwsExceptionIfRoleToSetDoesNotExistWithId() {
        // given
        UserRoleDTO roleDto = new UserRoleDTO(1);
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser()));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserRole(1, roleDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role")
                .hasMessageContaining("id");
    }

    @Test
    void throwsExceptionIfUserToSetRoleDoesNotExistWithId() {
        // given
        UserRoleDTO roleDto = new UserRoleDTO(1);

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserRole(1, roleDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("id");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithUpdateOwnPermissionCanSetUserImage() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(imageDto.getImageId(), null)));

        // when
        AppUser actual = underTest.setUserImage(userId, imageDto);

        // then
        assertThat(actual.getImage().getId()).isEqualTo(imageDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserWithoutUpdateOwnPermissionSetsUserImage() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId, new Role(Collections.emptyList()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(userId, imageDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithUpdateOtherPermissionCanSetUserImage() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(2, new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHER_USER)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(userId)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(imageDto.getImageId(), null)));

        // when
        AppUser actual = underTest.setUserImage(1, imageDto);

        // then
        assertThat(actual.getImage().getId()).isEqualTo(imageDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserWithoutUpdateOtherPermissionSetsUserImage() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(2, new Role(Collections.emptyList()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(1, imageDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserToSetImageDoesNotExistWithId() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(userId, imageDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfImageToSetDoesNotExistWithId() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(userId, imageDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithUpdateOwnPermissionCanSetUserPassword() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        UserPasswordDTO passwordDto = new UserPasswordDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn(newPassword);

        // when
        AppUser actual = underTest.setUserPassword(userId, passwordDto);

        // then
        assertThat(actual.getPassword()).isEqualTo(passwordDto.getNewPassword());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserWithoutUpdateOwnPermissionSetsUserPassword() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        UserPasswordDTO passwordDto = new UserPasswordDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId, new Role(Collections.emptyList()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(userId, passwordDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithUpdateOtherPermissionCanSetUserPassword() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        UserPasswordDTO passwordDto = new UserPasswordDTO(currentPassword, newPassword);
        AppUser user = new AppUser(2, new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHER_USER)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser(userId)));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn(newPassword);

        // when
        AppUser actual = underTest.setUserPassword(userId, passwordDto);

        // then
        assertThat(actual.getPassword()).isEqualTo(passwordDto.getNewPassword());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserWithoutUpdateOtherPermissionSetsUserPassword() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        UserPasswordDTO passwordDto = new UserPasswordDTO(currentPassword, newPassword);
        AppUser user = new AppUser(2, new Role(Collections.emptyList()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(userId, passwordDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfCurrentPasswordIsWrong() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        UserPasswordDTO passwordDto = new UserPasswordDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(userId, passwordDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Password");
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfUserToSetPasswordDoesNotExistWithId() {
        // given
        long userId = 1;
        UserPasswordDTO passwordDto = new UserPasswordDTO("", "");
        AppUser user = new AppUser(userId, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(userId, passwordDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userId));
    }
}