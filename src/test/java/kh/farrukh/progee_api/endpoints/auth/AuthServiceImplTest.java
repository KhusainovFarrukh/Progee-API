package kh.farrukh.progee_api.endpoints.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserDTO;
import kh.farrukh.progee_api.endpoints.user.UserService;
import kh.farrukh.progee_api.exception.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static kh.farrukh.progee_api.utils.constants.JWTKeys.KEY_ROLE_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
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
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private TokenProvider tokenProvider;
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
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_ROLE));
        when(roleRepository.findById(any())).thenReturn(Optional.of(role));
        AppUser existingUser = new AppUser("user@mail.com", 1, roleRepository);
        when(userService.loadUserByUsername(any())).thenReturn(existingUser);
        when(tokenProvider.getRefreshTokenAlgorithm()).thenReturn(Algorithm.HMAC256("test"));
        when(tokenProvider.getRefreshTokenValidityInSeconds()).thenReturn(604800L);
        String refreshToken = JWT.create()
                .withSubject("user@mail.com")
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenProvider.getRefreshTokenValidityInSeconds() * 1000))
                .withClaim(KEY_ROLE_ID, role.getPermissions().stream().map(Enum::name).toList())
                .sign(tokenProvider.getRefreshTokenAlgorithm());
        JWTVerifier jwtVerifier = JWT.require(tokenProvider.getRefreshTokenAlgorithm()).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
        when(tokenProvider.validateToken(anyString(), anyBoolean())).thenReturn(decodedJWT);
        when(tokenProvider.generateTokens(any())).thenReturn(
                new AuthResponse(
                        role,
                        "test",
                        "test",
                        "test",
                        "test"
                )
        );

        // when
        AuthResponse authResponse = underTest.refreshToken("Bearer " + refreshToken);

        // then
        assertThat(authResponse.getRole().getTitle()).isEqualTo(existingUser.getRole().getTitle());
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