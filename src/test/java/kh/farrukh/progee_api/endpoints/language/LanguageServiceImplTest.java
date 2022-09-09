package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.dto.ResourceStateDTO;
import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
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
class LanguageServiceImplTest {

    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private LanguageServiceImpl underTest;

    @Test
    void unauthenticatedUserCanGetApprovedLanguages() {
        // given
        SecurityContextHolder.clearContext();
        when(languageRepository.findAll(any(LanguageSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getLanguages(null, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findAll(
                new LanguageSpecification(ResourceState.APPROVED),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    void unauthenticatedUserCanNotGetLanguagesFilteredByState() {
        // given
        SecurityContextHolder.clearContext();

        // when
        // then
        assertThatThrownBy(() -> underTest.getLanguages(ResourceState.WAITING, 1, 10, "id", "ASC"))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(languageRepository, never()).findByState(any(), any());
    }

    @Test
    @WithMockUser
    void simpleUserCanGetApprovedLanguages() {
        // given
        when(languageRepository.findAll(any(LanguageSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getLanguages(null, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findAll(
                new LanguageSpecification(ResourceState.APPROVED),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void throwsExceptionIfUserWithoutRequiredPermissionFiltersLanguagesByState() {
        // when
        // then
        assertThatThrownBy(() -> underTest.getLanguages(ResourceState.WAITING, 1, 10, "id", "ASC"))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(languageRepository, never()).findByState(any(), any());
    }

    @Test
    @WithMockUser
    void userWithoutRequiredPermissionCanGetApprovedLanguages() {
        // given
        when(languageRepository.findAll(any(LanguageSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getLanguages(null, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findAll(
                new LanguageSpecification(ResourceState.APPROVED),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void userWithRequiredPermissionCanGetLanguagesFilteredByState() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_LANGUAGES_BY_STATE));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        when(languageRepository.findAll(any(LanguageSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getLanguages(ResourceState.WAITING, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findAll(
                new LanguageSpecification(ResourceState.WAITING),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser
    void userWithRequiredPermissionCanGetAllLanguages() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_LANGUAGES_BY_STATE));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        when(languageRepository.findAll(any(LanguageSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getLanguages(null, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findAll(
                new LanguageSpecification(null),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    void canGetLanguageById() {
        // given
        long id = 1;
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language()));

        // when
        underTest.getLanguageById(id);

        // then
        verify(languageRepository).findById(id);
    }

    @Test
    void throwsExceptionIfLanguageDoesNotExistWithId() {
        // given
        long id = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.getLanguageById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    @WithMockUser
    void userWithOnlyCreatePermissionCreatesLanguageWithWaitingState() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_CREATE_LANGUAGE));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO("", "", 1);
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.addLanguage(languageRequestDto);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language capturedLanguage = languageArgCaptor.getValue();
        assertThat(capturedLanguage.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser
    void userWithCreateAndSetStatePermissionsCreatesLanguageWithApprovedState() {
        // given
        Role role = new Role(List.of(Permission.CAN_CREATE_LANGUAGE, Permission.CAN_SET_LANGUAGE_STATE));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO("", "", 1);
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.addLanguage(languageRequestDto);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language capturedLanguage = languageArgCaptor.getValue();
        assertThat(capturedLanguage.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfLanguageExistsWithName() {
        // given
        String name = "test";
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO(name, "", 1);
        when(languageRepository.existsByName(name)).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addLanguage(languageRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser
    void authorWithOnlyUpdateOwnPermissionCanUpdateLanguage() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_LANGUAGE)));
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(author);
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateLanguage(1, languageRequestDto);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());
        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser
    void authorWithUpdateOwnAndSetStatePermissionsCanUpdateLanguage() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(List.of(Permission.CAN_UPDATE_OWN_LANGUAGE, Permission.CAN_SET_LANGUAGE_STATE)));
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(author);
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateLanguage(1, languageRequestDto);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());
        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser
    void throwsExceptionIfAuthorWithoutUpdateOwnPermissionUpdatesLanguage() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(author);
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageRequestDTO))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void nonAuthorWithOnlyUpdateOthersPermissionCanUpdateLanguage() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser user = new AppUser(2, new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHERS_LANGUAGE)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(new AppUser(1));
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateLanguage(1, languageRequestDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());
        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser
    void nonAuthorWithUpdateOthersAndSetStatePermissionsCanUpdateOthersLanguage() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser user = new AppUser(2, new Role(List.of(Permission.CAN_UPDATE_OTHERS_LANGUAGE, Permission.CAN_SET_LANGUAGE_STATE)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(new AppUser(1));
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.updateLanguage(1, languageRequestDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());
        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser
    void throwsExceptionIfNonAuthorWithoutUpdateOthersPermissionUpdatesLanguage() {
        // given
        AppUser user = new AppUser(1, new Role(Collections.emptyList()));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO("", "", 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(new AppUser(2));
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageRequestDTO))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfLanguageToUpdateDoesNotExistWithId() {
        // given
        long languageId = 1;
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO("", "", 1);
        when(languageRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(languageId, languageRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfLanguageToUpdateExistsWithName() {
        // given
        String name = "test name";
        AppUser user = new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_LANGUAGE)));
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(user);
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO(name, "", 1);
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(languageRepository.existsByName(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfImageOfLanguageToUpdateDoesNotExistWithId() {
        // given
        long imageId = 1;
        AppUser user = new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_LANGUAGE)));
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(user);
        LanguageRequestDTO languageRequestDto = new LanguageRequestDTO("", "", imageId);
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(imageId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canDeleteLanguageById() {
        // given
        long languageId = 1;
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        underTest.deleteLanguage(languageId);

        // then
        verify(languageRepository).deleteById(languageId);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfLanguageToDeleteDoesNotExistWithId() {
        // given
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteLanguage(languageId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canSetLanguageState() {
        // given
        long languageId = 1;
        ResourceStateDTO stateDto = new ResourceStateDTO(ResourceState.APPROVED);
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language()));

        // when
        underTest.setLanguageState(languageId, stateDto);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());
        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(stateDto.getState());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfLanguageToSetStateDoesNotExistWithId() {
        // given
        long languageId = 1;
        ResourceStateDTO stateDto = new ResourceStateDTO(ResourceState.APPROVED);
        when(languageRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.setLanguageState(languageId, stateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }
}