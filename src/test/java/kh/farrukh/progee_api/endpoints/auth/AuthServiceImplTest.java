package kh.farrukh.progee_api.endpoints.auth;

import com.auth0.jwt.JWT;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserDTO;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.endpoints.user.UserService;
import kh.farrukh.progee_api.exception.custom_exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static kh.farrukh.progee_api.security.utils.JWTUtils.algorithm;
import static kh.farrukh.progee_api.security.utils.JWTUtils.refreshValidMillis;
import static kh.farrukh.progee_api.utils.constant.JWTKeys.KEY_ROLE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SecurityTestExecutionListeners
class AuthServiceImplTest {

    @Mock
    private EmailValidator emailValidator;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthServiceImpl underTest;

    @Test
    void canRegister() {
        // given
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "test", "test_lover", "user@mail.com", "1234", 1
        );
        when(emailValidator.test(any())).thenReturn(true);

        // when
        underTest.register(registrationRequest);

        // then
        ArgumentCaptor<AppUserDTO> appUserDTOArgCaptor = ArgumentCaptor.forClass(AppUserDTO.class);
        verify(userService).addUser(appUserDTOArgCaptor.capture());

        AppUserDTO capturedAppUSerDto = appUserDTOArgCaptor.getValue();
        assertThat(capturedAppUSerDto.getEmail()).isEqualTo(registrationRequest.getEmail());
        assertThat(capturedAppUSerDto.getUsername()).isEqualTo(registrationRequest.getUsername());
        assertThat(capturedAppUSerDto.getName()).isEqualTo(registrationRequest.getName());
        assertThat(capturedAppUSerDto.getPassword()).isEqualTo(registrationRequest.getPassword());
        assertThat(capturedAppUSerDto.getImageId()).isEqualTo(registrationRequest.getImageId());
    }

    @Test
    void throwsExceptionIfEmailIsNotValid() {
        // given
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "test", "test_lover", "user-mail.com", "1234", 1
        );

        // when
        // then
        assertThatThrownBy(() -> underTest.register(registrationRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void canRefreshToken() {
        // given
        AppUser existingUser = new AppUser("user@mail.com", UserRole.USER);
        when(userService.loadUserByUsername(any())).thenReturn(existingUser);
        String refreshToken = JWT.create()
                .withSubject("user@mail.com")
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshValidMillis))
                .withClaim(KEY_ROLE, UserRole.USER.name())
                .sign(algorithm);

        // when
        AuthResponse authResponse = underTest.refreshToken("Bearer " + refreshToken);

        // then
        assertThat(authResponse.getRole()).isEqualTo(existingUser.getRole().name());
    }

    @Test
    void throwsExceptionIfRefreshTokenIsEmpty() {
        // given
        String refreshToken = "";

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Refresh token");
    }

    @Test
    void throwsExceptionIfRefreshTokenIsNull() {
        // given
        String refreshToken = null;

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Refresh token");
    }

    @Test
    void throwsExceptionIfRefreshTokenIsInvalid() {
        // given
        String refreshToken = "test";

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Refresh token");
    }
}