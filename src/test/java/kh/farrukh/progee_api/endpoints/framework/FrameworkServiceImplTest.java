package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.global.dto.ResourceStateDTO;
import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SecurityTestExecutionListeners
class FrameworkServiceImplTest {

    @Mock
    private FrameworkRepository frameworkRepository;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FrameworkServiceImpl underTest;

    @Test
    void unauthenticatedUserCanGetApprovedFrameworks() {
        // given
        SecurityContextHolder.clearContext();
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        underTest.getFrameworksByLanguage(1, null, 1, 10, "id", "ASC");

        // then
        verify(frameworkRepository).findByStateAndLanguage_Id(
                ResourceState.APPROVED,
                1,
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    void unauthenticatedUserCanNotGetFrameworksFilteredByState() {
        // given
        SecurityContextHolder.clearContext();
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.getFrameworksByLanguage(1, ResourceState.WAITING, 1, 10, "id", "ASC"))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(frameworkRepository, never()).findByStateAndLanguage_Id(any(), anyLong(), any());
    }

    @Test
    @WithMockUser
    void userWithoutRequiredPermissionCanGetApprovedFrameworks() {
        // given
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        underTest.getFrameworksByLanguage(1, null, 1, 10, "id", "ASC");

        // then
        verify(frameworkRepository).findByStateAndLanguage_Id(
                ResourceState.APPROVED,
                1,
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void throwsExceptionIfUserWithoutRequiredPermissionFiltersFrameworksByState() {
        // given
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(
                () -> underTest.getFrameworksByLanguage(1, ResourceState.WAITING, 1, 10, "id", "ASC")
        ).isInstanceOf(NotEnoughPermissionException.class);
    }

    //this test is unnecessary for current logic
    @Test
    @WithMockUser(username = "test@mail.com")
    void userWithRequiredPermissionCanGetApprovedFrameworks() {
        // given
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        underTest.getFrameworksByLanguage(1, null, 1, 10, "id", "ASC");

        // then
        verify(frameworkRepository).findByStateAndLanguage_Id(
                ResourceState.APPROVED,
                1,
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void userWithRequiredPermissionCanGetFrameworksFilteredByState() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));

        // when
        underTest.getFrameworksByLanguage(1, ResourceState.WAITING, 1, 10, "id", "ASC");

        // then
        verify(frameworkRepository).findByStateAndLanguage_Id(
                ResourceState.WAITING,
                1,
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void throwsExceptionIfLanguageOfFrameworksDoesNotExistWithId() {
        // given
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(
                () -> underTest.getFrameworksByLanguage(
                        languageId, null, 1, 10, "id", "ASC"
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    void canGetFrameworkById() {
        // given
        long frameworkId = 1;
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(new Framework()));

        // when
        underTest.getFrameworkById(1, frameworkId);

        // then
        verify(frameworkRepository).findById(frameworkId);
    }

    @Test
    void throwsExceptionIfFrameworkDoesNotExistWithId() {
        // given
        long frameworkId = 1;
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.getFrameworkById(1, frameworkId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    void throwsExceptionIfLanguageOfFrameworkDoesNotExistWithId() {
        // given
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.getFrameworkById(languageId, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser
    void userWithOnlyCreatePermissionCreatesFrameworkWithWaitingState() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_CREATE_FRAMEWORK));
        FrameworkDTO frameworkDto = new FrameworkDTO("", "", 1);
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language(1)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("user@mail.com", role)));

        // when
        underTest.addFramework(1, frameworkDto);

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
    void userWithCreateAndSetStatePermissionsCreatesFrameworkWithApprovedState() {
        // given
        Role role = new Role(List.of(Permission.CAN_CREATE_FRAMEWORK, Permission.CAN_SET_FRAMEWORK_STATE));
        AppUser user = new AppUser("user@mail.com", role);
        FrameworkDTO frameworkDto = new FrameworkDTO("", "", 1);
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language(1)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        underTest.addFramework(1, frameworkDto);

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
    void throwsExceptionIfFrameworkExistsWithName() {
        // given
        String name = "test";
        FrameworkDTO frameworkDto = new FrameworkDTO(name, "", 1);
        when(frameworkRepository.existsByName(name)).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addFramework(1, frameworkDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser
    void throwsExceptionIfLanguageOfFrameworkToCreateDoesNotExistWithId() {
        // given
        long languageId = 1;
        FrameworkDTO frameworkDto = new FrameworkDTO("", "", 1);
        when(languageRepository.findById(any())).thenReturn(Optional.empty());
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser()));

        // when
        // then
        assertThatThrownBy(() -> underTest.addFramework(languageId, frameworkDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser
    void authorWithOnlyUpdateOwnPermissionCanUpdateFramework() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_FRAMEWORK)));
        FrameworkDTO frameworkDto = new FrameworkDTO(name, desc, 1);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(author);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        Framework actual = underTest.updateFramework(1, 1, frameworkDto);

        // then
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser
    void authorWithUpdateOwnAndSetStatePermissionsCanUpdateFramework() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(List.of(Permission.CAN_UPDATE_OWN_FRAMEWORK, Permission.CAN_SET_FRAMEWORK_STATE)));
        FrameworkDTO frameworkDto = new FrameworkDTO(name, desc, 1);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(author);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        Framework actual = underTest.updateFramework(1, 1, frameworkDto);

        // then
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser
    void throwsExceptionIfAuthorWithoutUpdateOwnPermissionUpdatesFramework() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        FrameworkDTO frameworkDto = new FrameworkDTO(name, desc, 1);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(author);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, 1, frameworkDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void nonAuthorWithOnlyUpdateOthersPermissionCanUpdateFramework() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser user = new AppUser(2, new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHERS_FRAMEWORK)));
        FrameworkDTO frameworkDto = new FrameworkDTO(name, desc, 1);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(new AppUser(1));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        Framework actual = underTest.updateFramework(1, 1, frameworkDto);

        // then
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser
    void nonAuthorWithUpdateOthersAndSetStatePermissionsCanUpdateOthersFramework() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser user = new AppUser(2, new Role(List.of(Permission.CAN_UPDATE_OTHERS_FRAMEWORK, Permission.CAN_SET_FRAMEWORK_STATE)));
        FrameworkDTO frameworkDto = new FrameworkDTO(name, desc, 1);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(new AppUser(1));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        Framework actual = underTest.updateFramework(1, 1, frameworkDto);

        // then
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser
    void throwsExceptionIfNonAuthorWithoutUpdateOthersPermissionUpdatesFramework() {
        // given
        AppUser user = new AppUser(1,new Role(Collections.emptyList()));
        FrameworkDTO frameworkDto = new FrameworkDTO("", "", 1);
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(new AppUser(2));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, 1, frameworkDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void throwsExceptionIfFrameworkToUpdateDoesNotExistWithId() {
        // given
        long frameworkId = 1;
        FrameworkDTO frameworkDto = new FrameworkDTO("", "", 1);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, frameworkId, frameworkDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    @WithMockUser
    void throwsExceptionIfLanguageOfFrameworkToUpdateDoesNotExistWithId() {
        // given
        long languageId = 1;
        FrameworkDTO frameworkDto = new FrameworkDTO("", "", 1);

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(languageId, 1, frameworkDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser
    void throwsExceptionIfFrameworkToUpdateExistsWithName() {
        // given
        String name = "test name";
        AppUser user = new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_FRAMEWORK)));
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(user);
        FrameworkDTO frameworkDto = new FrameworkDTO(name, "", 1);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(languageRepository.existsByName(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, 1, frameworkDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser
    void throwsExceptionIfImageOfFrameworkToUpdateDoesNotExistWithId() {
        // given
        long imageId = 1;
        AppUser user = new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_FRAMEWORK)));
        Framework existingFramework = new Framework();
        existingFramework.setAuthor(user);
        FrameworkDTO frameworkDto = new FrameworkDTO("", "", imageId);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(existingFramework));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateFramework(1, 1, frameworkDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(imageId));
    }

    @Test
    @WithMockUser
    void canDeleteFrameworkById() {
        // given
        long frameworkId = 1;
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.existsById(any())).thenReturn(true);

        // when
        underTest.deleteFramework(1, frameworkId);

        // then
        verify(frameworkRepository).deleteById(frameworkId);
    }

    @Test
    @WithMockUser
    void throwsExceptionIfFrameworkToDeleteDoesNotExistWithId() {
        // given
        long frameworkId = 1;
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteFramework(1, frameworkId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    @WithMockUser
    void throwsExceptionIfLanguageOfFrameworkToDeleteDoesNotExistWithId() {
        // given
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteFramework(languageId, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser
    void canSetFrameworkState() {
        // given
        long frameworkId = 1;
        ResourceStateDTO stateDto = new ResourceStateDTO(ResourceState.APPROVED);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.of(new Framework()));

        // when
        Framework framework = underTest.setFrameworkState(1, frameworkId, stateDto);

        // then
        assertThat(framework.getState()).isEqualTo(stateDto.getState());
    }

    @Test
    @WithMockUser
    void throwsExceptionIfFrameworkToSetStateDoesNotExistWithId() {
        // given
        long frameworkId = 1;
        ResourceStateDTO stateDto = new ResourceStateDTO(ResourceState.APPROVED);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(frameworkRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.setFrameworkState(1, frameworkId, stateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Framework")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    @WithMockUser
    void throwsExceptionIfLanguageOfFrameworkToSetStateDoesNotExistWithId() {
        // given
        long languageId = 1;
        ResourceStateDTO stateDto = new ResourceStateDTO(ResourceState.APPROVED);

        // when
        // then
        assertThatThrownBy(() -> underTest.setFrameworkState(languageId, 1, stateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }
}