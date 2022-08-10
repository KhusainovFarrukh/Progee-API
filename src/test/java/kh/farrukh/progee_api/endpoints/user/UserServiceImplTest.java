package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
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
// TODO: 8/10/22
//                UserRole.USER,
                1
        );
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.addUser(userDto);

        // then
        ArgumentCaptor<AppUser> userArgCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userArgCaptor.capture());

        AppUser capturedUser = userArgCaptor.getValue();
        assertThat(capturedUser.getUniqueUsername()).isEqualTo(userDto.getUsername());
        assertThat(capturedUser.getEmail()).isEqualTo(userDto.getEmail());
        // TODO: 8/10/22
//        assertThat(capturedUser.getRole()).isEqualTo(userDto.getRole());
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
// TODO: 8/10/22
                1,
//                UserRole.USER,
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
                // TODO: 8/10/22
                1,
//                UserRole.USER,
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
    void canUpdateUser() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                // TODO: 8/10/22
                1,
//                UserRole.USER,
                1
        );
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser()));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        AppUser actual = underTest.updateUser(1, userDto);

        // then
        assertThat(actual.getName()).isEqualTo(userDto.getName());
        assertThat(actual.getUniqueUsername()).isEqualTo(userDto.getUsername());
        assertThat(actual.getEmail()).isEqualTo(userDto.getEmail());
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
                // TODO: 8/10/22
                1,
//                UserRole.USER,
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
    void throwsExceptionIfUsernameOfUserToUpdateIsAlreadyUsed() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                // TODO: 8/10/22
                1,
//                UserRole.USER,
                1
        );
        when(userRepository.existsByUniqueUsername(any())).thenReturn(true);
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser()));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(1, userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("username");
    }

    @Test
    void throwsExceptionIfEmailOfUserToUpdateIsAlreadyUsed() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                // TODO: 8/10/22
                1,
//                UserRole.USER,
                1
        );
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser()));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(1, userDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("email");
    }

    @Test
    void throwsExceptionIfImageOfUserToUpdateDoesNotExistWithId() {
        // given
        AppUserDTO userDto = new AppUserDTO(
                "user",
                "user@mail.com",
                "user_u",
                "1234",
                true,
                false,
                // TODO: 8/10/22
                1,
//                UserRole.USER,
                1
        );
        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser()));

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
        // TODO: 8/10/22
//        UserRoleDTO roleDto = new UserRoleDTO(UserRole.ADMIN);
//        when(userRepository.findById(any())).thenReturn(Optional.of(new AppUser()));
//
//        // when
//        AppUser actual = underTest.setUserRole(1, roleDto);
//
//        // then
//        assertThat(actual.getRole()).isEqualTo(roleDto.getRole());
    }

    @Test
    void throwsExceptionIfUserToSetRoleDoesNotExistWithId() {
        // given
        // TODO: 8/10/22
//        UserRoleDTO roleDto = new UserRoleDTO(UserRole.ADMIN);
//
//        // when
//        // then
//        assertThatThrownBy(() -> underTest.setUserRole(1, roleDto))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("User")
//                .hasMessageContaining("id");
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void selfCanSetUserImage() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(imageDto.getImageId(), null)));

        // when
        AppUser actual = underTest.setUserImage(userId, imageDto);

        // then
        assertThat(actual.getImage().getId()).isEqualTo(imageDto.getImageId());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "ADMIN")
    void adminCanSetUserImage() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId);
        // TODO: 8/10/22
//        user.setRole(UserRole.ADMIN);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(imageDto.getImageId(), null)));

        // when
        AppUser actual = underTest.setUserImage(2, imageDto);

        // then
        assertThat(actual.getImage().getId()).isEqualTo(imageDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfOtherUserSetsUserImage() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserImage(2, imageDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfUserToSetImageDoesNotExistWithId() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId);
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
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfImageToSetDoesNotExistWithId() {
        // given
        long userId = 1;
        UserImageDTO imageDto = new UserImageDTO(1);
        AppUser user = new AppUser(userId);
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
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void selfCanSetUserPassword() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        UserPasswordDTO passwordDto = new UserPasswordDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId);
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
    @WithMockUser(username = "admin@mail.com", authorities = "ADMIN")
    void adminCanSetUserPassword() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        UserPasswordDTO passwordDto = new UserPasswordDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId);
        // TODO: 8/10/22
//        user.setRole(UserRole.ADMIN);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn(newPassword);

        // when
        AppUser actual = underTest.setUserPassword(2, passwordDto);

        // then
        assertThat(actual.getPassword()).isEqualTo(passwordDto.getNewPassword());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfOtherUserSetsUserPassword() {
        // given
        long userId = 1;
        UserPasswordDTO passwordDto = new UserPasswordDTO("", "");
        AppUser user = new AppUser(userId);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.setUserPassword(2, passwordDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfCurrentPasswordIsWrong() {
        // given
        long userId = 1;
        String currentPassword = "1234";
        String newPassword = "4321";
        UserPasswordDTO passwordDto = new UserPasswordDTO(currentPassword, newPassword);
        AppUser user = new AppUser(userId);
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
        AppUser user = new AppUser(userId);
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