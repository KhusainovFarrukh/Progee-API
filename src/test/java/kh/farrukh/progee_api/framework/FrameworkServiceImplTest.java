package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.resource_state.SetResourceStateRequestDTO;
import kh.farrukh.progee_api.global.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SecurityTestExecutionListeners
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FrameworkServiceImplTest {

    @Mock
    private FrameworkRepository frameworkRepository;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @InjectMocks
    private FrameworkServiceImpl underTest;

    @Test
    @WithAnonymousUser
    void getFrameworks_canGetApprovedFrameworks_whenUnauthenticatedUser() {
        // given
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findAll(any(FrameworkSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getFrameworks(1L, null, 1, 10, "id", "ASC");

        // then
        verify(frameworkRepository).findAll(
                new FrameworkSpecification(1L, ResourceState.APPROVED),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithAnonymousUser
    void getFrameworks_canGetFrameworksFilteredByState_whenUnauthenticatedUser() {
        // given
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.getFrameworks(1L, ResourceState.WAITING, 1, 10, "id", "ASC"))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void getFrameworks_canGetApprovedFrameworks_whenUserWithoutRequiredPermission() {
        // given
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findAll(any(FrameworkSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getFrameworks(1L, null, 1, 10, "id", "ASC");

        // then
        verify(frameworkRepository).findAll(
                new FrameworkSpecification(1L, ResourceState.APPROVED),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void getFrameworks_throwsException_whenUserWithoutRequiredPermissionFiltersFrameworksByState() {
        // given
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(
                () -> underTest.getFrameworks(1L, ResourceState.WAITING, 1, 10, "id", "ASC")
        ).isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void getFrameworks_canGetAllFrameworks_whenUserWithRequiredPermission() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findAll(any(FrameworkSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getFrameworks(1L, null, 1, 10, "id", "ASC");

        // then
        verify(frameworkRepository).findAll(
                new FrameworkSpecification(1L, null),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void getFrameworks_canGetFrameworksFilteredByState_whenUserWithRequiredPermission() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        when(frameworkRepository.findAll(any(FrameworkSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getFrameworks(1L, ResourceState.WAITING, 1, 10, "id", "ASC");

        // then
        verify(frameworkRepository).findAll(
                new FrameworkSpecification(1L, ResourceState.WAITING),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void getFrameworks_throwsException_whenLanguageOfFrameworksDoesNotExistWithId() {
        // given
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(
                () -> underTest.getFrameworks(languageId, null, 1, 10, "id", "ASC")
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser
    void getFrameworkById_canGetApprovedFrameworkById_whenUserWithoutPermission() {
        // given
        long frameworkId = 1;
        when(frameworkRepository.findById(any()))
                .thenReturn(Optional.of(new Framework("test", ResourceState.APPROVED, new Language(1))));

        // when
        underTest.getFrameworkById(frameworkId);

        // then
        verify(frameworkRepository).findById(frameworkId);
    }

    @Test
    @WithMockUser
    void getFrameworkById_throwsException_whenUserWithoutPermission() {
        // given
        long frameworkId = 1;
        Role role = new Role(Collections.emptyList());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        when(frameworkRepository.findById(any()))
                .thenReturn(Optional.of(new Framework("test", ResourceState.WAITING, new Language(1))));

        // when
        // then
        assertThatThrownBy(() -> underTest.getFrameworkById(frameworkId))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void getFrameworkById_canGetNonApprovedFrameworkById_whenUserWithPermission() {
        // given
        long frameworkId = 1;
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE));
        when(frameworkRepository.findById(any()))
                .thenReturn(Optional.of(new Framework("test", ResourceState.WAITING, new Language(1))));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("user@mail.com", role)));

        // when
        underTest.getFrameworkById(frameworkId);

        // then
        verify(frameworkRepository).findById(frameworkId);
    }

    @Test
    void getFrameworkById_throwsException_whenFrameworkDoesNotExistWithId() {
        // given
        long frameworkId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.getFrameworkById(frameworkId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    @WithMockUser
    void addFramework_throwsException_whenLanguageIdIsNullOnRequestDto() {
        // given
        FrameworkRequestDTO requestDto = new FrameworkRequestDTO("Test", "Test", 1L);

        // when
        // then
        assertThatThrownBy(() -> underTest.addFramework(requestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Language id");
    }

    @Test
    @WithMockUser
    void addFramework_createsFrameworkWithWaitingState_whenUserWithOnlyCreatePermission() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_CREATE_FRAMEWORK));
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO("", "", 1, 1L);
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language(1)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("user@mail.com", role)));

        // when
        underTest.addFramework(frameworkRequestDto);

        // then
        ArgumentCaptor<Framework> languageArgCaptor = ArgumentCaptor.forClass(Framework.class);
        verify(frameworkRepository).save(languageArgCaptor.capture());

        Framework capturedFramework = languageArgCaptor.getValue();
        assertThat(capturedFramework.getLanguage().getId()).isEqualTo(1);
        assertThat(capturedFramework.getAuthor().getUsername()).isEqualTo("user@mail.com");
        assertThat(capturedFramework.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser
    void addFramework_createsFrameworkWithApprovedState_whenUserWithCreateAndSetStatePermissions() {
        // given
        Role role = new Role(List.of(Permission.CAN_CREATE_FRAMEWORK, Permission.CAN_SET_FRAMEWORK_STATE));
        AppUser user = new AppUser("user@mail.com", role);
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO("", "", 1, 1L);
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language(1)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        underTest.addFramework(frameworkRequestDto);

        // then
        ArgumentCaptor<Framework> languageArgCaptor = ArgumentCaptor.forClass(Framework.class);
        verify(frameworkRepository).save(languageArgCaptor.capture());

        Framework capturedFramework = languageArgCaptor.getValue();
        assertThat(capturedFramework.getLanguage().getId()).isEqualTo(1);
        assertThat(capturedFramework.getAuthor().getUsername()).isEqualTo("user@mail.com");
        assertThat(capturedFramework.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser
    void addFramework_throwsException_whenFrameworkExistsWithName() {
        // given
        String name = "test";
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO(name, "", 1, 1L);
        when(frameworkRepository.existsByName(name)).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addFramework(frameworkRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser
    void addFramework_throwsException_whenLanguageOfFrameworkToCreateDoesNotExistWithId() {
        // given
        long languageId = 1;
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO("", "", 1, languageId);
        when(languageRepository.findById(any())).thenReturn(Optional.empty());
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        // then
        assertThatThrownBy(() -> underTest.addFramework(frameworkRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser
    void updateFramework_canUpdateFramework_whenAuthorWithOnlyUpdateOwnPermission() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_FRAMEWORK)));
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO(name, desc, 1);
        Framework existingFramework = new Framework("test", ResourceState.APPROVED, new Language(1));
        existingFramework.setAuthor(author);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
//        Framework updatedFramework = new Framework(name, ResourceState.APPROVED, new Language(1));
//        updatedFramework.setAuthor(author);
//        updatedFramework.setName(frameworkDto.getName());
//        updatedFramework.setDescription(frameworkDto.getDescription());
//        updatedFramework.setImage(new Image(frameworkDto.getImageId()));
//        when(frameworkRepository.save(any())).thenReturn(updatedFramework);

        // when
        underTest.updateFramework(1, frameworkRequestDto);

        // then
        ArgumentCaptor<Framework> frameworkArgCaptor = ArgumentCaptor.forClass(Framework.class);
        verify(frameworkRepository).save(frameworkArgCaptor.capture());
        Framework actual = frameworkArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser
    void updateFramework_canUpdateFramework_whenAuthorWithUpdateOwnAndSetStatePermissions() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(List.of(Permission.CAN_UPDATE_OWN_FRAMEWORK, Permission.CAN_SET_FRAMEWORK_STATE)));
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO(name, desc, 1, 1L);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(author);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateFramework(1, frameworkRequestDto);

        // then
        ArgumentCaptor<Framework> frameworkArgCaptor = ArgumentCaptor.forClass(Framework.class);
        verify(frameworkRepository).save(frameworkArgCaptor.capture());
        Framework actual = frameworkArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser
    void updateFramework_throwsException_whenAuthorWithoutUpdateOwnPermissionUpdatesFramework() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO(name, desc, 1, 1L);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(author);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, frameworkRequestDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void updateFramework_canUpdateFramework_whenNonAuthorWithOnlyUpdateOthersPermission() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser user = new AppUser(2, new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHERS_FRAMEWORK)));
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO(name, desc, 1, 1L);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(new AppUser(1));
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateFramework(1, frameworkRequestDto);

        // then
        ArgumentCaptor<Framework> frameworkArgCaptor = ArgumentCaptor.forClass(Framework.class);
        verify(frameworkRepository).save(frameworkArgCaptor.capture());
        Framework actual = frameworkArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser
    void updateFramework_canUpdateOthersFramework_whenNonAuthorWithUpdateOthersAndSetStatePermissions() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser user = new AppUser(2, new Role(List.of(Permission.CAN_UPDATE_OTHERS_FRAMEWORK, Permission.CAN_SET_FRAMEWORK_STATE)));
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO(name, desc, 1, 1L);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(new AppUser(1));
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateFramework(1, frameworkRequestDto);

        // then
        ArgumentCaptor<Framework> frameworkArgCaptor = ArgumentCaptor.forClass(Framework.class);
        verify(frameworkRepository).save(frameworkArgCaptor.capture());
        Framework actual = frameworkArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser
    void updateFramework_throwsException_whenNonAuthorWithoutUpdateOthersPermission() {
        // given
        AppUser user = new AppUser(1, new Role(Collections.emptyList()));
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO("", "", 1, 1L);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(new AppUser(2));
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, frameworkRequestDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void updateFramework_throwsException_whenFrameworkToUpdateDoesNotExistWithId() {
        // given
        long frameworkId = 1;
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO("", "", 1, 1L);
        when(frameworkRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(frameworkId, frameworkRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    @WithMockUser
    void updateFramework_throwsException_whenFrameworkToUpdateExistsWithName() {
        // given
        String name = "test name";
        AppUser user = new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_FRAMEWORK)));
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(user);
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO(name, "", 1, 1L);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(languageRepository.existsByName(any())).thenReturn(true);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, frameworkRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser
    void updateFramework_throwsException_whenImageOfFrameworkToUpdateDoesNotExistWithId() {
        // given
        long imageId = 1;
        AppUser user = new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_FRAMEWORK)));
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(user);
        FrameworkRequestDTO frameworkRequestDto = new FrameworkRequestDTO("", "", imageId, 1L);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, frameworkRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(imageId));
    }

    @Test
    @WithMockUser
    void deleteFramework_canDeleteFrameworkById_whenIdIsValid() {
        // given
        long frameworkId = 1;
        when(frameworkRepository.existsById(any())).thenReturn(true);

        // when
        underTest.deleteFramework(frameworkId);

        // then
        verify(frameworkRepository).deleteById(frameworkId);
    }

    @Test
    @WithMockUser
    void deleteFramework_throwsException_whenFrameworkToDeleteDoesNotExistWithId() {
        // given
        long frameworkId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteFramework(frameworkId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    @WithMockUser
    void setFrameworkState_canSetFrameworkState_whenSetResourceStateRequestDTO() {
        // given
        long frameworkId = 1;
        SetResourceStateRequestDTO stateDto = new SetResourceStateRequestDTO(ResourceState.APPROVED);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(new Framework()));

        // when
        underTest.setFrameworkState(frameworkId, stateDto);

        // then
        ArgumentCaptor<Framework> frameworkArgCaptor = ArgumentCaptor.forClass(Framework.class);
        verify(frameworkRepository).save(frameworkArgCaptor.capture());
        Framework actual = frameworkArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(stateDto.getState());
    }

    @Test
    @WithMockUser
    void setFrameworkState_throwsException_whenFrameworkToSetStateDoesNotExistWithId() {
        // given
        long frameworkId = 1;
        SetResourceStateRequestDTO stateDto = new SetResourceStateRequestDTO(ResourceState.APPROVED);
        when(frameworkRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.setFrameworkState(frameworkId, stateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(String.valueOf(frameworkId));
    }
}