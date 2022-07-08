package kh.farrukh.progee_api.endpoints.language;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LANGUAGE;
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
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private LanguageService languageService;


    @AfterEach
    void tearDown() {
        languageRepository.deleteAll();
        userRepository.deleteAll();
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
                response.getItems().stream().map(Language::getName).collect(Collectors.toList())
                        .contains(language.getName())
        )).isTrue();
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "ADMIN")
    void canGetLanguagesWithFilter() throws Exception {
        // given
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
                response.getItems().stream().map(Language::getName).collect(Collectors.toList())
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
        assertThat(language.getId()).isEqualTo(language.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canAddLanguage() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser("user@mail.com", UserRole.USER));
        Image existingImage = imageRepository.save(new Image());
        LanguageDTO languageDto = new LanguageDTO("test", "test", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_LANGUAGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        Language language = objectMapper.readValue(result.getResponse().getContentAsString(), Language.class);
        assertThat(language.getName()).isEqualTo(languageDto.getName());
        assertThat(language.getDescription()).isEqualTo(languageDto.getDescription());
        assertThat(language.getImage().getId()).isEqualTo(languageDto.getImageId());
        assertThat(language.getAuthor().getId()).isEqualTo(existingUser.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canUpdateLanguage() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser("user@mail.com", UserRole.USER));
        Image existingImage = imageRepository.save(new Image());
        Language existingLanguage = languageService.addLanguage(new LanguageDTO("test", "test", existingImage.getId()));
        LanguageDTO languageDto = new LanguageDTO("test-update", "test-update", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Language language = objectMapper.readValue(result.getResponse().getContentAsString(), Language.class);
        assertThat(language.getId()).isEqualTo(existingLanguage.getId());
        assertThat(language.getName()).isEqualTo(languageDto.getName());
        assertThat(language.getDescription()).isEqualTo(languageDto.getDescription());
        assertThat(language.getImage().getId()).isEqualTo(languageDto.getImageId());
        assertThat(language.getAuthor().getId()).isEqualTo(existingUser.getId());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "ADMIN")
    void canDeleteUserById() throws Exception {
        // given
        userRepository.save(new AppUser("admin@mail.com", UserRole.ADMIN));
        Image existingImage = imageRepository.save(new Image());
        Language existingLanguage = languageService.addLanguage(new LanguageDTO("", "", existingImage.getId()));

        // when
        // then
        mvc.perform(delete(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "ADMIN")
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