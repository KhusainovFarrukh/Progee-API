package kh.farrukh.progee_api.app_user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.app_user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.app_user.payloads.SetUserPasswordRequestDTO;
import kh.farrukh.progee_api.app_user.payloads.SetUserRoleRequestDTO;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static kh.farrukh.progee_api.app_user.AppUserController.ENDPOINT_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AppUserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void tearDown() {
        appUserRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canGetUsers() throws Exception {
        // given
        List<AppUser> users = List.of(
                new AppUser("user1@mail.com"),
                new AppUser("user2@mail.com"),
                new AppUser("user3@mail.com")
        );
        appUserRepository.saveAll(users);

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
    @WithMockUser(username = "user@mail.com")
    void canGetUserById() throws Exception {
        // given
        AppUser existingUser = appUserRepository.save(new AppUser());

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
    @WithMockUser(username = "user@mail.com")
    void canUpdateUser() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        AppUserRequestDTO userDto = new AppUserRequestDTO(
                "test",
                "test@mail.com",
                "test",
                null,
                true,
                false,
                1,
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
        assertThat(user.getUniqueUsername()).isEqualTo(userDto.getUniqueUsername());
        assertThat(user.getImage().getId()).isEqualTo(userDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canDeleteUserById() throws Exception {
        // given
        AppUser existingUser = appUserRepository.save(new AppUser());

        // when
        // then
        mvc.perform(delete(ENDPOINT_USER + "/" + existingUser.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canSetUserRole() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role("1", false, Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        Role newRole = roleRepository.save(new Role("2", false, Collections.singletonList(Permission.CAN_UPDATE_OTHER_USER)));
        AppUser existingUser = appUserRepository.save(new AppUser(
                "user@mail.com", "test", existingRole.getId(), roleRepository
        ));
        SetUserRoleRequestDTO roleDto = new SetUserRoleRequestDTO(newRole.getId());

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
        assertThat(user.getRole().getId()).isEqualTo(roleDto.getRoleId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canSetUserPassword() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role("1", false, Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        String password = "12345678";
        String newPassword = "87654321";
        AppUser existingUser = appUserRepository.save(new AppUser(
                "user@mail.com", "test", passwordEncoder.encode(password), existingRole
        ));
        SetUserPasswordRequestDTO passwordDto = new SetUserPasswordRequestDTO(password, newPassword);

        // when
        mvc.perform(patch(ENDPOINT_USER + "/" + existingUser.getId() + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        existingUser = appUserRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(passwordDto.getNewPassword(), existingUser.getPassword())).isTrue();
    }
}