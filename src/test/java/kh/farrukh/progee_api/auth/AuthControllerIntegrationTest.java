package kh.farrukh.progee_api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.app_user.payloads.AppUserResponseDTO;
import kh.farrukh.progee_api.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.auth.payloads.LoginRequestDTO;
import kh.farrukh.progee_api.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.Collections;

import static kh.farrukh.progee_api.auth.AuthConstants.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.progee_api.auth.AuthConstants.ENDPOINT_REGISTRATION;
import static kh.farrukh.progee_api.global.security.utils.AuthenticationFilterConfigurer.ENDPOINT_LOGIN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @AfterEach
    void tearDown() {
        appUserRepository.deleteAll();
        imageRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void register_canRegister() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role("user", true, Collections.singletonList(Permission.CAN_VIEW_ROLE)));
        Image existingImage = imageRepository.save(new Image());
        RegistrationRequestDTO request = new RegistrationRequestDTO(
                "test", "test_user", "user@mail.com", "12345678", existingImage.getId()
        );

        // when
        MvcResult result = mvc
                .perform(
                        post(ENDPOINT_REGISTRATION)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AppUserResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), AppUserResponseDTO.class);
        assertThat(actual.getName()).isEqualTo(request.getName());
        assertThat(actual.getUniqueUsername()).isEqualTo(request.getUniqueUsername());
        assertThat(actual.getEmail()).isEqualTo(request.getEmail());
        assertThat(actual.getImage().getId()).isEqualTo(request.getImageId());
        assertThat(actual.getRole().getId()).isEqualTo(existingRole.getId());
    }

    @Test
    void login_canLogin() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role("user", true, Collections.singletonList(Permission.CAN_VIEW_ROLE)));
        AppUser user = appUserRepository.save(
                new AppUser("user@mail.com", existingRole.getId(), passwordEncoder.encode("12345678"), roleRepository)
        );
        LoginRequestDTO request = new LoginRequestDTO(user.getEmail(), "12345678");

        // when
        MvcResult result = mvc
                .perform(
                        post(ENDPOINT_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AuthResponseDTO actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponseDTO.class
        );

        assertThat(actual.getRole().getTitle()).isEqualTo(user.getRole().getTitle());
    }

    @Test
    void refreshToken_canRefreshToken() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VIEW_ROLE)));
        AppUser existingUser = appUserRepository.save(
                new AppUser("user@mail.com", existingRole.getId(), passwordEncoder.encode("12345678"), roleRepository)
        );

        // when
        MvcResult result = mvc
                .perform(
                        get(ENDPOINT_REFRESH_TOKEN)
                                .header("Authorization", "Bearer " + tokenProvider.createRefreshToken(
                                        existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getRefreshTokenValidityInSeconds())
                                ))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AuthResponseDTO actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponseDTO.class
        );
        assertThat(actual.getRole().getTitle()).isEqualTo(existingUser.getRole().getTitle());
        assertThat(actual.getAccessToken()).isNotNull();
        assertThat(actual.getRefreshToken()).isNotNull();
    }
}