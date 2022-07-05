package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
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

    // TODO: 7/5/22 add updateLanguage tests

    // TODO: 7/5/22 add deleteLanguage tests

    // TODO: 7/5/22 add setLanguageState tests
}