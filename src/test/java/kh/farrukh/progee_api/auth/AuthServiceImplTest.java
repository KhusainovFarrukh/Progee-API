package kh.farrukh.progee_api.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserService;
import kh.farrukh.progee_api.app_user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.token_exceptions.UnknownTokenException;
import kh.farrukh.progee_api.global.security.jwt.JwtConfiguration;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static kh.farrukh.progee_api.global.security.jwt.JWTKeys.KEY_ROLE_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private EmailValidator emailValidator;
    @Mock
    private AppUserService appUserService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private TokenProvider tokenProvider;
    @InjectMocks
    private AuthServiceImpl underTest;

    @Test
    void canRegister() {
        // given
        RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO(
                "test", "test_lover", "user@mail.com", "1234", 1
        );
        when(emailValidator.test(any())).thenReturn(true);
        when(roleRepository.findFirstByIsDefaultIsTrue()).thenReturn(Optional.of(new Role()));

        // when
        underTest.register(registrationRequestDTO);

        // then
        ArgumentCaptor<AppUserRequestDTO> appUserDTOArgCaptor = ArgumentCaptor.forClass(AppUserRequestDTO.class);
        verify(appUserService).addUser(appUserDTOArgCaptor.capture());

        AppUserRequestDTO capturedAppUSerRequestDto = appUserDTOArgCaptor.getValue();
        assertThat(capturedAppUSerRequestDto.getEmail()).isEqualTo(registrationRequestDTO.getEmail());
        assertThat(capturedAppUSerRequestDto.getUniqueUsername()).isEqualTo(registrationRequestDTO.getUniqueUsername());
        assertThat(capturedAppUSerRequestDto.getName()).isEqualTo(registrationRequestDTO.getName());
        assertThat(capturedAppUSerRequestDto.getPassword()).isEqualTo(registrationRequestDTO.getPassword());
        assertThat(capturedAppUSerRequestDto.getImageId()).isEqualTo(registrationRequestDTO.getImageId());
    }

    @Test
    void throwsExceptionIfEmailIsNotValid() {
        // given
        RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO(
                "test", "test_lover", "user-mail.com", "1234", 1
        );

        // when
        // then
        assertThatThrownBy(() -> underTest.register(registrationRequestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void canRefreshToken() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_ROLE));
        when(roleRepository.findById(any())).thenReturn(Optional.of(role));
        AppUser existingUser = new AppUser("user@mail.com", 1, roleRepository);
        when(tokenProvider.getRefreshTokenAlgorithm()).thenReturn(Algorithm.HMAC256("test"));
        JwtConfiguration jwtConfiguration = new JwtConfiguration();
        jwtConfiguration.setRefreshTokenValidityInSeconds(604800L);
        when(tokenProvider.getJwtConfiguration()).thenReturn(jwtConfiguration);
        String refreshToken = JWT.create()
                .withSubject("user@mail.com")
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenProvider.getJwtConfiguration().getRefreshTokenValidityInSeconds() * 1000))
                .withClaim(KEY_ROLE_ID, role.getPermissions().stream().map(Enum::name).toList())
                .sign(tokenProvider.getRefreshTokenAlgorithm());
        JWTVerifier jwtVerifier = JWT.require(tokenProvider.getRefreshTokenAlgorithm()).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
        when(tokenProvider.validateToken(anyString(), anyBoolean())).thenReturn(decodedJWT);
        when(tokenProvider.generateTokens(any())).thenReturn(
                new AuthResponseDTO(
                        role,
                        "test",
                        "test",
                        ZonedDateTime.now(),
                        ZonedDateTime.now()
                )
        );

        // when
        AuthResponseDTO authResponseDTO = underTest.refreshToken("Bearer " + refreshToken);

        // then
        assertThat(authResponseDTO.getRole().getTitle()).isEqualTo(existingUser.getRole().getTitle());
    }

    @Test
    void throwsExceptionIfRefreshTokenIsEmpty() {
        // given
        String refreshToken = "";

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(UnknownTokenException.class);
    }

    @Test
    void throwsExceptionIfRefreshTokenIsNull() {
        // given
        String refreshToken = null;

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(UnknownTokenException.class);
    }

    @Test
    void throwsExceptionIfRefreshTokenIsInvalid() {
        // given
        String refreshToken = "test";

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(refreshToken))
                .isInstanceOf(UnknownTokenException.class);
    }
}