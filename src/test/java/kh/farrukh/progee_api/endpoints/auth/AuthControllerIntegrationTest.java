package kh.farrukh.progee_api.endpoints.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.endpoints.user.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_REGISTRATION;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

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
    @Disabled
    void canRefreshToken() {
        // TODO: 7/7/22 refresh token integration test
    }
}