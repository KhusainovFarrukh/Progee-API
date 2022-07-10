package kh.farrukh.progee_api.endpoints.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.security.utils.LoginRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.*;
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
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void canRegister() throws Exception {
        // given
        Image existingImage = imageRepository.save(new Image());
        RegistrationRequest request = new RegistrationRequest(
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
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void canRefreshToken() throws Exception {
        // TODO: 7/7/22 refresh token integration test
        // given
        AppUser user = userRepository.save(
                new AppUser("user@mail.com", UserRole.USER, passwordEncoder.encode("12345678"))
        );
        LoginRequest request = new LoginRequest(user.getEmail(), "12345678");

        MvcResult loginResult = mvc
                .perform(
                        post(ENDPOINT_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class
        );

        // when
        MvcResult result = mvc
                .perform(
                        get(ENDPOINT_REFRESH_TOKEN)
                                .header("Authorization", "Bearer " + loginResponse.getRefreshToken())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class
        );
        assertThat(response.getRole()).isEqualTo(user.getRole().name());
    }
}