package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
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

        // when
        underTest.getLanguages(null, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findByState(
                ResourceState.APPROVED,
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
    @WithMockUser(authorities = {"USER"})
    void simpleUserCanGetApprovedLanguages() {
        // when
        underTest.getLanguages(null, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findByState(
                ResourceState.APPROVED,
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void throwsExceptionIfSimpleUserFiltersLanguagesByState() {
        // when
        // then
        assertThatThrownBy(() -> underTest.getLanguages(ResourceState.WAITING, 1, 10, "id", "ASC"))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(languageRepository, never()).findByState(any(), any());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void adminCanGetApprovedLanguages() {
        // when
        underTest.getLanguages(null, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findByState(
                ResourceState.APPROVED,
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void adminCanGetLanguagesFilteredByState() {
        // when
        underTest.getLanguages(ResourceState.WAITING, 1, 10, "id", "ASC");

        // then
        verify(languageRepository).findByState(
                ResourceState.WAITING,
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
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void simpleUserCreatesLanguageWithWaitingState() {
        // given
        LanguageDTO languageDto = new LanguageDTO("", "", 1);
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("user@mail.com")));

        // when
        underTest.addLanguage(languageDto);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language capturedLanguage = languageArgCaptor.getValue();
        assertThat(capturedLanguage.getAuthor().getUsername()).isEqualTo("user@mail.com");
        assertThat(capturedLanguage.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "ADMIN")
    void adminCreatesLanguageWithApprovedState() {
        // given
        LanguageDTO languageDto = new LanguageDTO("", "", 1);
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("admin@mail.com")));

        // when
        underTest.addLanguage(languageDto);

        // then
        ArgumentCaptor<Language> languageArgCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageArgCaptor.capture());

        Language capturedLanguage = languageArgCaptor.getValue();
        assertThat(capturedLanguage.getAuthor().getUsername()).isEqualTo("admin@mail.com");
        assertThat(capturedLanguage.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfLanguageExistsWithName() {
        // given
        String name = "test";
        LanguageDTO languageDto = new LanguageDTO(name, "", 1);
        when(languageRepository.existsByName(name)).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addLanguage(languageDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = {"USER"})
    void authorCanUpdateLanguage() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser author = new AppUser(1);
        LanguageDTO languageDto = new LanguageDTO(name, desc, 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(author);
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        Language actual = underTest.updateLanguage(1, languageDto);

        // then
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.WAITING);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = {"ADMIN"})
    void adminCanUpdateLanguage() {
        // given
        String name = "test name";
        String desc = "test desc";
        AppUser admin = new AppUser(2);
        admin.setRole(UserRole.ADMIN);
        LanguageDTO languageDto = new LanguageDTO(name, desc, 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(new AppUser(1));
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(admin));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        Language actual = underTest.updateLanguage(1, languageDto);

        // then
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDescription()).isEqualTo(desc);
        assertThat(actual.getState()).isEqualTo(ResourceState.APPROVED);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = {"USER"})
    void throwsExceptionIfNonAuthorUpdatesLanguage() {
        // given
        AppUser user = new AppUser(1);
        LanguageDTO languageDto = new LanguageDTO("", "", 1);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(new AppUser(2));
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = {"ADMIN"})
    void throwsExceptionIfLanguageToUpdateDoesNotExistWithId() {
        // given
        long languageId = 1;
        LanguageDTO languageDto = new LanguageDTO("", "", 1);
        when(languageRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(languageId, languageDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = {"ADMIN"})
    void throwsExceptionIfLanguageToUpdateExistsWithName() {
        // given
        String name = "test name";
        AppUser admin = new AppUser(1);
        admin.setRole(UserRole.ADMIN);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(admin);
        LanguageDTO languageDto = new LanguageDTO(name, "", 1);
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(languageRepository.existsByName(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(admin));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(name);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = {"ADMIN"})
    void throwsExceptionIfImageOfLanguageToUpdateDoesNotExistWithId() {
        // given
        long imageId = 1;
        AppUser admin = new AppUser(1);
        admin.setRole(UserRole.ADMIN);
        Language existingLanguage = new Language();
        existingLanguage.setAuthor(admin);
        LanguageDTO languageDto = new LanguageDTO("", "", imageId);
        when(languageRepository.findById(any())).thenReturn(Optional.of(existingLanguage));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(admin));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLanguage(1, languageDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(imageId));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = {"ADMIN"})
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
    @WithMockUser(username = "admin@mail.com", authorities = {"ADMIN"})
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

    // TODO: 7/5/22 add setLanguageState tests
    @Test
    @WithMockUser(username = "admin@mail.com", authorities = {"ADMIN"})
    void canSetLanguageState() {
        // given
        long languageId = 1;
        ResourceStateDTO stateDto = new ResourceStateDTO(ResourceState.APPROVED);
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language()));

        // when
        Language language = underTest.setLanguageState(languageId, stateDto);

        // then
        assertThat(language.getState()).isEqualTo(stateDto.getState());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = {"ADMIN"})
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