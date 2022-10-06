package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.resource_state.SetResourceStateRequestDTO;
import kh.farrukh.progee_api.global.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.payloads.LanguageRequestDTO;
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
import static org.mockito.Mockito.*;

@SecurityTestExecutionListeners
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class LanguageServiceImplTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private LanguageServiceImpl underTest;

    @Test
    @WithAnonymousUser
    void getLanguages_canGetApprovedLanguages_whenUnauthenticatedUser() {
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
    @WithAnonymousUser
    void getLanguages_throwsException_whenUnauthenticatedUserTriesToFilterLanguagesByState() {
        // when
        // then
        assertThatThrownBy(() -> underTest.getLanguages(ResourceState.WAITING, 1, 10, "id", "ASC"))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(languageRepository, never()).findByState(any(), any());
    }

    @Test
    @WithMockUser
    void getLanguages_canGetApprovedLanguages_whenUserWithoutRequiredPermission() {
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
    void getLanguages_throwsException_whenUserWithoutRequiredPermissionFiltersLanguagesByState() {
        // when
        // then
        assertThatThrownBy(() -> underTest.getLanguages(ResourceState.WAITING, 1, 10, "id", "ASC"))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(languageRepository, never()).findByState(any(), any());
    }

    @Test
    @WithMockUser
    void getLanguages_canGetLanguagesFilteredByState_whenUserWithRequiredPermission() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_LANGUAGES_BY_STATE));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
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
    void getLanguages_canGetAllLanguages_whenUserWithRequiredPermission() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_VIEW_LANGUAGES_BY_STATE));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
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
    @WithAnonymousUser
    void getLanguageById_canGetLanguageById_whenStateIsApproved() {
        // given
        long id = 1;
        when(languageRepository.findById(id))
                .thenReturn(Optional.of(new Language("test", ResourceState.APPROVED)));

        // when
        underTest.getLanguageById(id);

        // then
        verify(languageRepository).findById(id);
    }

    @Test
    @WithMockUser
    void getLanguageById_throwsException_whenUserWithoutRequiredPermissionTriesToGetsNonApprovedLanguageById() {
        // given
        long id = 1;
        Role role = new Role(Collections.emptyList());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        when(languageRepository.findById(id)).thenReturn(Optional.of(new Language("test", ResourceState.WAITING)));

        // when
        // then
        assertThatThrownBy(() -> underTest.getLanguageById(id))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithAnonymousUser
    void getLanguageById_throwsException_whenLanguageDoesNotExistWithId() {
        // given
        long id = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.getLanguageById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    @WithMockUser
    void addLanguage_createsLanguageWithWaitingState_whenUserWithOnlyCreatePermission() {
        // given
        Role role = new Role(Collections.singletonList(Permission.CAN_CREATE_LANGUAGE));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO("test", "test", 1);
        when(imageRepository.findById(languageRequestDTO.getImageId()))
                .thenReturn(Optional.of(new Image(languageRequestDTO.getImageId())));

        // when
        underTest.addLanguage(languageRequestDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
        assertThat(actual.getName()).isEqualTo(languageRequestDTO.getName());
        assertThat(actual.getDescription()).isEqualTo(languageRequestDTO.getDescription());
        assertThat(actual.getImage().getId()).isEqualTo(languageRequestDTO.getImageId());
    }

    @Test
    @WithMockUser
    void addLanguage_createsLanguageWithApprovedState_whenUserWithCreateAndSetStatePermissions() {
        // given
        Role role = new Role(List.of(Permission.CAN_CREATE_LANGUAGE, Permission.CAN_SET_LANGUAGE_STATE));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("test@mail.com", role)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO("test", "test", 1);
        when(imageRepository.findById(languageRequestDTO.getImageId()))
                .thenReturn(Optional.of(new Image(languageRequestDTO.getImageId())));

        // when
        underTest.addLanguage(languageRequestDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
        assertThat(actual.getName()).isEqualTo(languageRequestDTO.getName());
        assertThat(actual.getDescription()).isEqualTo(languageRequestDTO.getDescription());
        assertThat(actual.getImage().getId()).isEqualTo(languageRequestDTO.getImageId());
    }

    @Test
    @WithMockUser
    void addLanguage_throwsException_whenLanguageExistsWithName() {
        // given
        String name = "test";
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, "test", 1);
        when(languageRepository.existsByName(name)).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addLanguage(languageRequestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("name")
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser
    void updateLanguage_canUpdateLanguageAndSetWaitingState_whenAuthorWithOnlyUpdateOwnPermission() {
        // given
        long id = 1;
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_LANGUAGE)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language(author);
        when(languageRepository.findById(id)).thenReturn(Optional.of(existingLanguage));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(languageRequestDTO.getImageId()))
                .thenReturn(Optional.of(new Image(languageRequestDTO.getImageId())));

        // when
        underTest.updateLanguage(id, languageRequestDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getImage().getId()).isEqualTo(languageRequestDTO.getImageId());
    }

    @Test
    @WithMockUser
    void updateLanguage_canUpdateLanguageAndSetApprovedState_whenAuthorWithUpdateOwnAndSetStatePermissions() {
        // given
        long id = 1;
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(List.of(Permission.CAN_UPDATE_OWN_LANGUAGE, Permission.CAN_SET_LANGUAGE_STATE)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language(author);
        when(languageRepository.findById(id)).thenReturn(Optional.of(existingLanguage));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(languageRequestDTO.getImageId()))
                .thenReturn(Optional.of(new Image(languageRequestDTO.getImageId())));

        // when
        underTest.updateLanguage(id, languageRequestDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getImage().getId()).isEqualTo(languageRequestDTO.getImageId());
    }

    @Test
    @WithMockUser
    void updateLanguage_throwsException_whenAuthorWithoutUpdateOwnPermissionUpdatesLanguage() {
        // given
        long id = 1;
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language(author);
        when(languageRepository.findById(id)).thenReturn(Optional.of(existingLanguage));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(id, languageRequestDTO))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(languageRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void updateLanguage_canUpdateLanguageAndSetWaitingState_whenNonAuthorWithOnlyUpdateOthersPermission() {
        // given
        long id = 1;
        String name = "test name";
        String desc = "test desc";
        AppUser user = new AppUser(2, new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHERS_LANGUAGE)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language(new AppUser(1));
        when(languageRepository.findById(id)).thenReturn(Optional.of(existingLanguage));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(languageRequestDTO.getImageId()))
                .thenReturn(Optional.of(new Image(languageRequestDTO.getImageId())));

        // when
        underTest.updateLanguage(id, languageRequestDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getImage().getId()).isEqualTo(languageRequestDTO.getImageId());
    }

    @Test
    @WithMockUser
    void updateLanguage_canUpdateOthersLanguageAndSetApprovedState_whenNonAuthorWithUpdateOthersAndSetStatePermissions() {
        // given
        long id = 1;
        String name = "test name";
        String desc = "test desc";
        AppUser user = new AppUser(2, new Role(List.of(Permission.CAN_UPDATE_OTHERS_LANGUAGE, Permission.CAN_SET_LANGUAGE_STATE)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, desc, 1);
        Language existingLanguage = new Language(new AppUser(1));
        when(languageRepository.findById(id)).thenReturn(Optional.of(existingLanguage));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(imageRepository.findById(languageRequestDTO.getImageId()))
                .thenReturn(Optional.of(new Image(languageRequestDTO.getImageId())));

        // when
        underTest.updateLanguage(id, languageRequestDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getImage().getId()).isEqualTo(languageRequestDTO.getImageId());
    }

    @Test
    @WithMockUser
    void updateLanguage_throwsException_whenNonAuthorWithoutUpdateOthersPermission() {
        // given
        long id = 1;
        AppUser user = new AppUser(1, new Role(Collections.emptyList()));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO("", "", 1);
        Language existingLanguage = new Language(new AppUser(2));
        when(languageRepository.findById(id)).thenReturn(Optional.of(existingLanguage));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(id, languageRequestDTO))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(languageRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void updateLanguage_throwsException_whenLanguageToUpdateDoesNotExistWithId() {
        // given
        long id = 1;
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO("test", "test", 1);
        when(languageRepository.findById(id)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(id, languageRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
        verify(languageRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void updateLanguage_throwsException_whenLanguageToUpdateExistsWithName() {
        // given
        long id = 1;
        String name = "test name";
        AppUser user = new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_LANGUAGE)));
        Language existingLanguage = new Language(user);
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(name, "test", 1);
        when(languageRepository.findById(id)).thenReturn(Optional.of(existingLanguage));
        when(languageRepository.existsByName(name)).thenReturn(true);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageRequestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("name")
                .hasMessageContaining(name);
        verify(languageRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void updateLanguage_throwsException_whenImageOfLanguageToUpdateDoesNotExistWithId() {
        // given
        long id = 1;
        long imageId = 1;
        AppUser user = new AppUser(1, new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_LANGUAGE)));
        Language existingLanguage = new Language(user);
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO("test", "test", imageId);
        when(languageRepository.findById(id)).thenReturn(Optional.of(existingLanguage));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(imageId));
        verify(languageRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void deleteLanguage_canDeleteLanguageById_whenIdIsValid() {
        // given
        long id = 1;
        when(languageRepository.existsById(id)).thenReturn(true);

        // when
        underTest.deleteLanguage(id);

        // then
        verify(languageRepository).deleteById(id);
    }

    @Test
    @WithMockUser
    void deleteLanguage_throwsException_whenLanguageToDeleteDoesNotExistWithId() {
        // given
        long id = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteLanguage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    @WithMockUser
    void setLanguageState_canSetLanguageState() {
        // given
        long id = 1;
        SetResourceStateRequestDTO stateDTO = new SetResourceStateRequestDTO(ResourceState.APPROVED);
        when(languageRepository.findById(id)).thenReturn(Optional.of(new Language()));

        // when
        underTest.setLanguageState(id, stateDTO);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language actual = languageArgCaptor.getValue();
        assertThat(actual.getState()).isEqualTo(stateDTO.getState());
    }

    @Test
    @WithMockUser
    void setLanguageState_throwsException_whenLanguageToSetStateDoesNotExistWithId() {
        // given
        long id = 1;
        SetResourceStateRequestDTO stateDTO = new SetResourceStateRequestDTO(ResourceState.APPROVED);
        when(languageRepository.findById(id)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.setLanguageState(id, stateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }
}