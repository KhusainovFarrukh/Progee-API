package kh.farrukh.progee_api.endpoints.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
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

import java.util.List;
import java.util.stream.Collectors;

import static kh.farrukh.progee_api.utils.constants.ApiEndpoints.ENDPOINT_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerIntegrationTest {

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

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canGetUsers() throws Exception {
        // given
        List<AppUser> users = List.of(
                new AppUser("user1@mail.com"),
                new AppUser("user2@mail.com"),
                new AppUser("user3@mail.com")
        );
        userRepository.saveAll(users);

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_USER).param("page_size", String.valueOf(users.size())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<AppUser> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(users.size());
        assertThat(users.stream().allMatch(user ->
                response.getItems().stream().map(AppUser::getEmail).collect(Collectors.toList())
                        .contains(user.getEmail())
        )).isTrue();
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canGetUserById() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser());

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_USER + "/" + existingUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AppUser user = objectMapper.readValue(result.getResponse().getContentAsString(), AppUser.class);
        assertThat(user.getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "SUPER_ADMIN")
    void canUpdateUser() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser("user@mail.com", "test"));
        Image existingImage = imageRepository.save(new Image());
        AppUserDTO userDto = new AppUserDTO(
                "test",
                "test@mail.com",
                "test",
                null,
                true,
                false,
                null,
                existingImage.getId()
        );

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_USER + "/" + existingUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AppUser user = objectMapper.readValue(result.getResponse().getContentAsString(), AppUser.class);
        assertThat(user.getId()).isEqualTo(existingUser.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(user.getUniqueUsername()).isEqualTo(userDto.getUsername());
        assertThat(user.getImage().getId()).isEqualTo(userDto.getImageId());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "SUPER_ADMIN")
    void canDeleteUserById() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser());

        // when
        // then
        mvc.perform(delete(ENDPOINT_USER + "/" + existingUser.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "SUPER_ADMIN")
    void canSetUserRole() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser("user@mail.com", "test", UserRole.USER));
        UserRoleDTO roleDto = new UserRoleDTO(UserRole.ADMIN);

        // when
        MvcResult result = mvc
                .perform(patch(ENDPOINT_USER + "/" + existingUser.getId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AppUser user = objectMapper.readValue(result.getResponse().getContentAsString(), AppUser.class);
        assertThat(user.getId()).isEqualTo(existingUser.getId());
        assertThat(user.getRole()).isEqualTo(roleDto.getRole());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "SUPER_ADMIN")
    void canSetUserPassword() throws Exception {
        // given
        userRepository.save(new AppUser("admin@mail.com", UserRole.SUPER_ADMIN));
        String password = "12345678";
        String newPassword = "87654321";
        AppUser existingUser = userRepository.save(new AppUser(
                "admin@mail.com", "test", passwordEncoder.encode(password)
        ));
        UserPasswordDTO passwordDto = new UserPasswordDTO(password, newPassword);

        // when
        mvc.perform(patch(ENDPOINT_USER + "/" + existingUser.getId() + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        existingUser = userService.getUserById(existingUser.getId());
        assertThat(passwordEncoder.matches(passwordDto.getNewPassword(), existingUser.getPassword())).isTrue();
    }
}