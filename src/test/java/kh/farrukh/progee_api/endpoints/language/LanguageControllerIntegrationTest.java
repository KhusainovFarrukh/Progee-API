package kh.farrukh.progee_api.endpoints.language;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.language.LanguageService;
import kh.farrukh.progee_api.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.resource_state.ResourceStateDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static kh.farrukh.progee_api.language.LanguageController.ENDPOINT_LANGUAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
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


    @AfterEach
    void tearDown() {
        languageRepository.deleteAll();
        appUserRepository.deleteAll();
        imageRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
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
        PagingResponse<Language> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(approvedLanguages.size());
        assertThat(approvedLanguages.stream().allMatch(language ->
                response.getItems().stream().map(Language::getName).toList()
                        .contains(language.getName())
        )).isTrue();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canGetLanguagesWithFilter() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VIEW_LANGUAGES_BY_STATE)));
        appUserRepository.save(new AppUser("user@mail.com", existingRole));
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
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<Language> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(waitingLanguages.size());
        assertThat(waitingLanguages.stream().allMatch(language ->
                response.getItems().stream().map(Language::getName).toList()
                        .contains(language.getName())
        )).isTrue();
    }

    @Test
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
        Language language = objectMapper.readValue(result.getResponse().getContentAsString(), Language.class);
        assertThat(language.getId()).isEqualTo(existingLanguage.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canAddLanguage() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_CREATE_LANGUAGE)));
        appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO("test", "test", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_LANGUAGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        Language language = objectMapper.readValue(result.getResponse().getContentAsString(), Language.class);
        assertThat(language.getName()).isEqualTo(languageRequestDto.getName());
        assertThat(language.getDescription()).isEqualTo(languageRequestDto.getDescription());
        assertThat(language.getImage().getId()).isEqualTo(languageRequestDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canUpdateLanguage() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_LANGUAGE)));
        appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        LanguageResponseDTO existingLanguage = languageService.addLanguage(new LanguageRequestDTO("test", "test", existingImage.getId()));
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO("test-update", "test-update", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Language language = objectMapper.readValue(result.getResponse().getContentAsString(), Language.class);
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
        appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        LanguageResponseDTO existingLanguage = languageService.addLanguage(new LanguageRequestDTO("", "", existingImage.getId()));

        // when
        // then
        mvc.perform(delete(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void canSetLanguageState() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language("", ResourceState.WAITING));
        ResourceStateDTO stateDto = new ResourceStateDTO(ResourceState.APPROVED);

        // when
        MvcResult result = mvc
                .perform(patch(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Language language = objectMapper.readValue(result.getResponse().getContentAsString(), Language.class);
        assertThat(language.getId()).isEqualTo(existingLanguage.getId());
        assertThat(language.getState()).isEqualTo(stateDto.getState());
    }
}