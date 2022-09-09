package kh.farrukh.progee_api.endpoints.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.endpoints.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.endpoints.auth.payloads.LoginRequestDTO;
import kh.farrukh.progee_api.endpoints.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static kh.farrukh.progee_api.endpoints.auth.AuthController.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.progee_api.endpoints.auth.AuthController.ENDPOINT_REGISTRATION;
import static kh.farrukh.progee_api.security.utils.AuthenticationFilterConfigurer.ENDPOINT_LOGIN;
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

    @AfterEach
    void tearDown() {
        appUserRepository.deleteAll();
        imageRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void canRegister() throws Exception {
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
        AppUser user = objectMapper.readValue(result.getResponse().getContentAsString(), AppUser.class);
        assertThat(user.getName()).isEqualTo(request.getName());
        assertThat(user.getUniqueUsername()).isEqualTo(request.getUsername());
        assertThat(user.getEmail()).isEqualTo(request.getEmail());
        assertThat(user.getImage().getId()).isEqualTo(request.getImageId());
        assertThat(user.getRole().getId()).isEqualTo(existingRole.getId());
    }

    @Test
    void canLogin() throws Exception {
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
        AuthResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponseDTO.class
        );

        assertThat(response.getRole().getTitle()).isEqualTo(user.getRole().getTitle());
    }

    // TODO: 7/29/22 giving 403 error if refresh token is valid
    @Test
    void canRefreshToken() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VIEW_ROLE)));
        AppUser user = appUserRepository.save(
                new AppUser("user@mail.com", existingRole.getId(), passwordEncoder.encode("12345678"), roleRepository)
        );
        LoginRequestDTO request = new LoginRequestDTO(user.getEmail(), "12345678");

        MvcResult loginResult = mvc
                .perform(
                        post(ENDPOINT_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDTO loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponseDTO.class
        );

        // when
        MvcResult result = mvc
                .perform(
                        get(ENDPOINT_REFRESH_TOKEN)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.getRefreshToken())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AuthResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponseDTO.class
        );
        assertThat(response.getRole().getTitle()).isEqualTo(user.getRole().getTitle());
    }
}