package kh.farrukh.progee_api.framework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.framework.payloads.FrameworkResponseDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.resource_state.SetResourceStateRequestDTO;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static kh.farrukh.progee_api.framework.FrameworkConstants.ENDPOINT_FRAMEWORK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @AfterEach
    void tearDown() {
        frameworkRepository.deleteAll();
        languageRepository.deleteAll();
        appUserRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void getFrameworks_canGetFrameworks_withoutStateAndLanguageIdFilter() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        List<Framework> approvedFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("test1", ResourceState.APPROVED, existingLanguage),
                new Framework("test2", ResourceState.APPROVED, existingLanguage),
                new Framework("test3", ResourceState.APPROVED, existingLanguage)
        ));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_FRAMEWORK)
                        .param("page_size", String.valueOf(approvedFrameworks.size())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<FrameworkResponseDTO> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(actual.getTotalItems()).isEqualTo(approvedFrameworks.size());
        List<Long> expectedIds = approvedFrameworks.stream().map(Framework::getId).toList();
        assertThat(actual.getItems().stream().allMatch(
                frameworkResponseDTO -> expectedIds.contains(frameworkResponseDTO.getId())
        )).isTrue();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void getFrameworks_canGetFrameworks_withStateAndWithoutLanguageIdFilter() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        List<Framework> waitingFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("test2", ResourceState.WAITING, existingLanguage),
                new Framework("test3", ResourceState.WAITING, existingLanguage)
        ));
        List<Framework> approvedFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("test1", ResourceState.APPROVED, existingLanguage)
        ));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_FRAMEWORK)
                        .param("page_size", String.valueOf(waitingFrameworks.size() + approvedFrameworks.size()))
                        .param("state", ResourceState.WAITING.name())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<FrameworkResponseDTO> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(actual.getTotalItems()).isEqualTo(waitingFrameworks.size());
        List<Long> expectedIds = waitingFrameworks.stream().map(Framework::getId).toList();
        assertThat(actual.getItems().stream().allMatch(
                frameworkResponseDTO -> expectedIds.contains(frameworkResponseDTO.getId()) &&
                        frameworkResponseDTO.getState().equals(ResourceState.WAITING)
        )).isTrue();
    }

    @Test
    @WithAnonymousUser
    void getFrameworks_canGetFrameworks_withoutStateAndWithLanguageIdFilter() throws Exception {
        // given
        Language existingLanguage1 = languageRepository.save(new Language());
        Language existingLanguage2 = languageRepository.save(new Language());
        List<Framework> language1Frameworks = frameworkRepository.saveAll(List.of(
                new Framework("test2", ResourceState.APPROVED, existingLanguage1),
                new Framework("test3", ResourceState.APPROVED, existingLanguage1)
        ));
        List<Framework> language2Frameworks = frameworkRepository.saveAll(List.of(
                new Framework("test1", ResourceState.APPROVED, existingLanguage2)
        ));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_FRAMEWORK)
                        .param("language_id", String.valueOf(existingLanguage1.getId()))
                        .param("page_size", String.valueOf(language1Frameworks.size() + language2Frameworks.size())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<FrameworkResponseDTO> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(actual.getTotalItems()).isEqualTo(language1Frameworks.size());
        List<Long> expectedIds = language1Frameworks.stream().map(Framework::getId).toList();
        assertThat(actual.getItems().stream().allMatch(
                frameworkResponseDTO -> expectedIds.contains(frameworkResponseDTO.getId()) &&
                        frameworkResponseDTO.getLanguage().getId() == existingLanguage1.getId()
        )).isTrue();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void getFrameworks_canGetFrameworks_withStateAndLanguageIdFilter() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage1 = languageRepository.save(new Language());
        Language existingLanguage2 = languageRepository.save(new Language());
        List<Framework> language1ApprovedFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("test1", ResourceState.APPROVED, existingLanguage1),
                new Framework("test2", ResourceState.APPROVED, existingLanguage1)
        ));
        List<Framework> language1WaitingFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("test3", ResourceState.WAITING, existingLanguage1),
                new Framework("test4", ResourceState.WAITING, existingLanguage1)
        ));
        List<Framework> language2ApprovedFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("test5", ResourceState.APPROVED, existingLanguage2)
        ));
        List<Framework> language2WaitingFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("test6", ResourceState.WAITING, existingLanguage2)
        ));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_FRAMEWORK)
                        .param("language_id", String.valueOf(existingLanguage1.getId()))
                        .param("page_size", String.valueOf(
                                language1ApprovedFrameworks.size() +
                                        language1WaitingFrameworks.size() +
                                        language2ApprovedFrameworks.size() +
                                        language2WaitingFrameworks.size()
                        ))
                        .param("state", ResourceState.WAITING.name())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<FrameworkResponseDTO> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );

        assertThat(actual.getTotalItems()).isEqualTo(language1WaitingFrameworks.size());
        List<Long> expectedIds = language1WaitingFrameworks.stream().map(Framework::getId).toList();
        assertThat(actual.getItems().stream().allMatch(
                frameworkResponseDTO -> expectedIds.contains(frameworkResponseDTO.getId()) &&
                        frameworkResponseDTO.getState().equals(ResourceState.WAITING) &&
                        frameworkResponseDTO.getLanguage().getId() == existingLanguage1.getId()
        )).isTrue();
    }

    @Test
    @WithAnonymousUser
    void getFrameworkById_canGetFrameworkById_whenIdIsValid() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        Framework existingFramework = frameworkRepository.save(
                new Framework("test", ResourceState.APPROVED, existingLanguage)
        );

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_FRAMEWORK + "/" + existingFramework.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        FrameworkResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), FrameworkResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingFramework.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void addFramework_canAddFramework_whenFrameworkRequestDTOIsValid() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_CREATE_FRAMEWORK)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        Language existingLanguage = languageRepository.save(new Language());
        FrameworkRequestDTO languageDto = new FrameworkRequestDTO("test", "test", existingImage.getId(), existingLanguage.getId());

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_FRAMEWORK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        FrameworkResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), FrameworkResponseDTO.class);
        assertThat(actual.getName()).isEqualTo(languageDto.getName());
        assertThat(actual.getDescription()).isEqualTo(languageDto.getDescription());
        assertThat(actual.getImage().getId()).isEqualTo(languageDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void updateFramework_canUpdateFramework_whenFrameworkRequestDTOIsValid() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_FRAMEWORK)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Image existingImage = imageRepository.save(new Image());
        Language existingLanguage = languageRepository.save(new Language());
        Framework existingFramework = frameworkRepository.save(
                new Framework("test", "test", existingImage, existingUser, ZonedDateTime.now(), existingLanguage)
        );
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO("test-update", "test-update", existingImage.getId());

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_FRAMEWORK + "/" + existingFramework.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(frameworkRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        FrameworkResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), FrameworkResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingFramework.getId());
        assertThat(actual.getName()).isEqualTo(frameworkRequestDto.getName());
        assertThat(actual.getDescription()).isEqualTo(frameworkRequestDto.getDescription());
        assertThat(actual.getImage().getId()).isEqualTo(frameworkRequestDto.getImageId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void deleteFramework_canDeleteFrameworkById_whenIdIsValid() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_DELETE_FRAMEWORK)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Framework existingFramework = frameworkRepository.save(new Framework());

        // when
        // then
        mvc.perform(delete(ENDPOINT_FRAMEWORK + "/" + existingFramework.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void setFrameworkState_canSetFrameworkState_whenSetResourceStateRequestDTOIsValid() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_SET_FRAMEWORK_STATE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Framework existingFramework = frameworkRepository.save(new Framework(ResourceState.WAITING));
        SetResourceStateRequestDTO stateDto = new SetResourceStateRequestDTO(ResourceState.APPROVED);

        // when
        MvcResult result = mvc
                .perform(patch(ENDPOINT_FRAMEWORK + "/" + existingFramework.getId() + "/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        ))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        FrameworkResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), FrameworkResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingFramework.getId());
        assertThat(actual.getState()).isEqualTo(stateDto.getState());
    }
}