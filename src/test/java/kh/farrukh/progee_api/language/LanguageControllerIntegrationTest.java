package kh.farrukh.progee_api.language;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.resource_state.SetResourceStateRequestDTO;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static kh.farrukh.progee_api.language.LanguageConstants.ENDPOINT_LANGUAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LanguageControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private TokenProvider tokenProvider;


    @AfterEach
    void tearDown() {
        languageRepository.deleteAll();
        appUserRepository.deleteAll();
        imageRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void canGetLanguagesWithoutFilter() throws Exception {
        // given
        List<Language> approvedLanguages = List.of(
                new Language("test1", ResourceState.APPROVED),
                new Language("test2", ResourceState.APPROVED),
                new Language("test3", ResourceState.APPROVED)
        );
        languageRepository.saveAll(approvedLanguages);

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_LANGUAGE).param("page_size", String.valueOf(approvedLanguages.size())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<LanguageResponseDTO> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(approvedLanguages.size());
        assertThat(approvedLanguages.stream().allMatch(language ->
                response.getItems().stream().map(LanguageResponseDTO::getName).toList()
                        .contains(language.getName())
        )).isTrue();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canGetLanguagesWithFilter() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VIEW_LANGUAGES_BY_STATE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        List<Language> waitingLanguages = List.of(
                new Language("test2", ResourceState.WAITING),
                new Language("test3", ResourceState.WAITING)
        );
        List<Language> approvedLanguages = List.of(
                new Language("test1", ResourceState.APPROVED)
        );
        languageRepository.saveAll(waitingLanguages);

        // when
        MvcResult result = mvc
                .perform(
                        get(ENDPOINT_LANGUAGE)
                                .param("page_size", String.valueOf(waitingLanguages.size() + approvedLanguages.size()))
                                .param("state", ResourceState.WAITING.name())
                                .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                        existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                                ))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<LanguageResponseDTO> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(waitingLanguages.size());
        assertThat(waitingLanguages.stream().allMatch(language ->
                response.getItems().stream().map(LanguageResponseDTO::getName).toList()
                        .contains(language.getName())
        )).isTrue();
    }

    @Test
    @WithAnonymousUser
    void canGetLanguageById() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language("test", ResourceState.APPROVED));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        LanguageResponseDTO language = objectMapper.readValue(result.getResponse().getContentAsString(), LanguageResponseDTO.class);
        assertThat(language.getId()).isEqualTo(existingLanguage.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canAddLanguage() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_CREATE_LANGUAGE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO("test", "test", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_LANGUAGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageRequestDto))
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        LanguageResponseDTO language = objectMapper.readValue(result.getResponse().getContentAsString(), LanguageResponseDTO.class);
        assertThat(language.getName()).isEqualTo(languageRequestDto.getName());
        assertThat(language.getDescription()).isEqualTo(languageRequestDto.getDescription());
        assertThat(language.getImage().getId()).isEqualTo(languageRequestDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canUpdateLanguage() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_LANGUAGE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        LanguageResponseDTO existingLanguage = languageService.addLanguage(new LanguageRequestDTO("test", "test", existingImage.getId()));
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO("test-update", "test-update", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageRequestDto))
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        LanguageResponseDTO language = objectMapper.readValue(result.getResponse().getContentAsString(), LanguageResponseDTO.class);
        assertThat(language.getId()).isEqualTo(existingLanguage.getId());
        assertThat(language.getName()).isEqualTo(languageRequestDto.getName());
        assertThat(language.getDescription()).isEqualTo(languageRequestDto.getDescription());
        assertThat(language.getImage().getId()).isEqualTo(languageRequestDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canDeleteUserById() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_DELETE_LANGUAGE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        LanguageResponseDTO existingLanguage = languageService.addLanguage(new LanguageRequestDTO("", "", existingImage.getId()));

        // when
        // then
        mvc.perform(delete(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId())
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canSetLanguageState() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_SET_LANGUAGE_STATE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language("", ResourceState.WAITING));
        SetResourceStateRequestDTO stateDto = new SetResourceStateRequestDTO(ResourceState.APPROVED);

        // when
        MvcResult result = mvc
                .perform(patch(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateDto))
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        LanguageResponseDTO language = objectMapper.readValue(result.getResponse().getContentAsString(), LanguageResponseDTO.class);
        assertThat(language.getId()).isEqualTo(existingLanguage.getId());
        assertThat(language.getState()).isEqualTo(stateDto.getState());
    }
}