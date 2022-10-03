package kh.farrukh.progee_api.app_user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.app_user.payloads.*;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static kh.farrukh.progee_api.app_user.AppUserConstants.ENDPOINT_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppUserControllerIntegrationTest {

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
        roleRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void getUsers_canGetUsers() throws Exception {
        // given
        List<AppUser> users = appUserRepository.saveAll(List.of(new AppUser(), new AppUser(), new AppUser()));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_USER).param("page_size", String.valueOf(users.size())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<AppUserResponseDTO> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(actual.getTotalItems()).isEqualTo(users.size());
        List<Long> expectedIds = users.stream().map(AppUser::getId).toList();
        assertThat(actual.getItems().stream().allMatch(userDTO -> expectedIds.contains(userDTO.getId()))).isTrue();
    }

    @Test
    @WithAnonymousUser
    void getUserById_canGetUserById() throws Exception {
        // given
        AppUser existingUser = appUserRepository.save(new AppUser());

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_USER + "/" + existingUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AppUserResponseDTO actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AppUserResponseDTO.class
        );
        assertThat(actual.getId()).isEqualTo(actual.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void updateUser_canUpdateUser() throws Exception {
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
                        .content(objectMapper.writeValueAsString(userDto))
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AppUserResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), AppUserResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingUser.getId());
        assertThat(actual.getName()).isEqualTo(userDto.getName());
        assertThat(actual.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(actual.getUniqueUsername()).isEqualTo(userDto.getUniqueUsername());
        assertThat(actual.getImage().getId()).isEqualTo(userDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void deleteUserById_canDeleteUserById() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_DELETE_USER)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        AppUser user = appUserRepository.save(new AppUser());

        // when
        // then
        mvc.perform(delete(ENDPOINT_USER + "/" + user.getId())
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(appUserRepository.existsById(user.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void setUserImage_canSetUserImage() throws Exception {
        // given
        Role role = roleRepository.save(new Role("1", false, Collections.singletonList(Permission.CAN_UPDATE_OWN_USER)));
        Image existingImage = imageRepository.save(new Image());
        Image newImage = imageRepository.save(new Image());
        AppUser existingUser = appUserRepository.save(new AppUser(
                "user@mail.com", role.getId(), roleRepository, existingImage.getId(), imageRepository)
        );
        SetUserImageRequestDTO imageDTO = new SetUserImageRequestDTO(newImage.getId());

        // when
        MvcResult result = mvc
                .perform(patch(ENDPOINT_USER + "/" + existingUser.getId() + "/image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(imageDTO))
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AppUserResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), AppUserResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingUser.getId());
        assertThat(actual.getImage().getId()).isEqualTo(imageDTO.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void setUserRole_canSetUserRole() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role("1", false, Collections.singletonList(Permission.CAN_SET_USER_ROLE)));
        Role newRole = roleRepository.save(new Role("2", false, Collections.singletonList(Permission.CAN_SET_USER_ROLE)));
        AppUser existingUser = appUserRepository.save(new AppUser(
                "user@mail.com", "test", existingRole.getId(), roleRepository
        ));
        SetUserRoleRequestDTO roleDto = new SetUserRoleRequestDTO(newRole.getId());

        // when
        MvcResult result = mvc
                .perform(patch(ENDPOINT_USER + "/" + existingUser.getId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        AppUserResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), AppUserResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingUser.getId());
        assertThat(actual.getRole().getId()).isEqualTo(roleDto.getRoleId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void setUserPassword_canSetUserPassword() throws Exception {
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
                        .content(objectMapper.writeValueAsString(passwordDto))
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        existingUser = appUserRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(passwordDto.getNewPassword(), existingUser.getPassword())).isTrue();
    }
}