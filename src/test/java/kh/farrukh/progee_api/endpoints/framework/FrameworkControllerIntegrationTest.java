package kh.farrukh.progee_api.endpoints.framework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static kh.farrukh.progee_api.utils.constants.ApiEndpoints.ENDPOINT_LANGUAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class FrameworkControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private FrameworkRepository frameworkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FrameworkService frameworkService;

    @AfterEach
    void tearDown() {
        frameworkRepository.deleteAll();
        languageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void canGetFrameworksWithoutFilter() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        List<Framework> approvedFrameworks = List.of(
                new Framework("test1", ResourceState.APPROVED, existingLanguage),
                new Framework("test2", ResourceState.APPROVED, existingLanguage),
                new Framework("test3", ResourceState.APPROVED, existingLanguage)
        );
        frameworkRepository.saveAll(approvedFrameworks);

        // when
        MvcResult result = mvc
                .perform(
                        get(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/frameworks")
                                .param("page_size", String.valueOf(approvedFrameworks.size()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<Framework> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(approvedFrameworks.size());
        assertThat(approvedFrameworks.stream().allMatch(framework ->
                response.getItems().stream().map(Framework::getName).toList()
                        .contains(framework.getName())
        )).isTrue();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canGetFrameworksWithFilter() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE)));
        userRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        List<Framework> waitingFrameworks = List.of(
                new Framework("test2", ResourceState.WAITING, existingLanguage),
                new Framework("test3", ResourceState.WAITING, existingLanguage)
        );
        List<Framework> approvedFrameworks = List.of(
                new Framework("test1", ResourceState.APPROVED, existingLanguage)
        );
        frameworkRepository.saveAll(waitingFrameworks);

        // when
        MvcResult result = mvc
                .perform(
                        get(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/frameworks")
                                .param("page_size", String.valueOf(waitingFrameworks.size() + approvedFrameworks.size()))
                                .param("state", ResourceState.WAITING.name())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<Framework> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(waitingFrameworks.size());
        assertThat(waitingFrameworks.stream().allMatch(framework ->
                response.getItems().stream().map(Framework::getName).collect(Collectors.toList())
                        .contains(framework.getName())
        )).isTrue();
    }

    @Test
    void canGetFrameworkById() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        Framework existingFramework = frameworkRepository.save(
                new Framework("test", ResourceState.APPROVED, existingLanguage)
        );

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_LANGUAGE + "/"
                        + existingLanguage.getId() +
                        "/frameworks/" + existingFramework.getId()
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Framework framework = objectMapper.readValue(result.getResponse().getContentAsString(), Framework.class);
        assertThat(framework.getId()).isEqualTo(existingFramework.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canAddFramework() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_CREATE_FRAMEWORK)));
        userRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        Language existingLanguage = languageRepository.save(new Language());
        FrameworkDTO languageDto = new FrameworkDTO("test", "test", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/frameworks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        Framework framework = objectMapper.readValue(result.getResponse().getContentAsString(), Framework.class);
        assertThat(framework.getName()).isEqualTo(languageDto.getName());
        assertThat(framework.getDescription()).isEqualTo(languageDto.getDescription());
        assertThat(framework.getImage().getId()).isEqualTo(languageDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canUpdateFramework() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_FRAMEWORK)));
        userRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        Language existingLanguage = languageRepository.save(new Language());
        Framework existingFramework = frameworkService.addFramework(
                existingLanguage.getId(), new FrameworkDTO("test", "test", existingImage.getId())
        );
        FrameworkDTO frameworkDto = new FrameworkDTO("test-update", "test-update", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/frameworks/" + existingFramework.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(frameworkDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Framework framework = objectMapper.readValue(result.getResponse().getContentAsString(), Framework.class);
        assertThat(framework.getId()).isEqualTo(existingFramework.getId());
        assertThat(framework.getName()).isEqualTo(frameworkDto.getName());
        assertThat(framework.getDescription()).isEqualTo(frameworkDto.getDescription());
        assertThat(framework.getImage().getId()).isEqualTo(frameworkDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canDeleteFrameworkById() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_DELETE_FRAMEWORK)));
        userRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        Language existingLanguage = languageRepository.save(new Language());
        Framework existingFramework = frameworkService.addFramework(
                existingLanguage.getId(), new FrameworkDTO("", "", existingImage.getId())
        );

        // when
        // then
        mvc.perform(delete(ENDPOINT_LANGUAGE + "/"
                        + existingLanguage.getId()
                        + "/frameworks/" + existingFramework.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canSetFrameworkState() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        Framework existingFramework = frameworkRepository.save(new Framework("", ResourceState.WAITING, existingLanguage));
        ResourceStateDTO stateDto = new ResourceStateDTO(ResourceState.APPROVED);

        // when
        MvcResult result = mvc
                .perform(patch(ENDPOINT_LANGUAGE + "/"
                        + existingLanguage.getId() + "/frameworks/"
                        + existingFramework.getId() + "/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Framework framework = objectMapper.readValue(result.getResponse().getContentAsString(), Framework.class);
        assertThat(framework.getId()).isEqualTo(existingFramework.getId());
        assertThat(framework.getState()).isEqualTo(stateDto.getState());
    }
}