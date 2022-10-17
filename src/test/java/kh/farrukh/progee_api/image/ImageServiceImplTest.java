package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static kh.farrukh.progee_api.image.ImageServiceImpl.IMAGES_FOLDER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private S3Repository s3Repository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageServiceImpl underTest;

    @Test
    void getUsers_canGetUsers() {
        // given
        when(imageRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getImages(1, 10);

        // then
        verify(imageRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    void addImage_canAddImage_whenFileIsValid() {
        // given
        MultipartFile mockFile = new MockMultipartFile("test.png", new byte[]{});
        when(s3Repository.savePublicReadObject(any(), any())).thenReturn("https://test.com");

        // when
        underTest.addImage(mockFile);

        // then
        ArgumentCaptor<Image> imageArgCaptor = ArgumentCaptor.forClass(Image.class);
        verify(imageRepository).save(imageArgCaptor.capture());

        Image actual = imageArgCaptor.getValue();
        assertThat(actual.getUrl()).isEqualTo("https://test.com");
    }

    @Test
    void getImageById_canGetImageById_whenIdIsValid() {
        // given
        long id = 1;
        when(imageRepository.findById(id)).thenReturn(Optional.of(new Image()));

        // when
        underTest.getImageById(id);

        // then
        verify(imageRepository).findById(id);
    }

    @Test
    void getImageById_throwsException_whenImageDoesNotExistWithId() {
        // given
        long id = 1;
        when(imageRepository.findById(id)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getImageById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    void deleteById_canDeleteUserById() {
        // given
        long id = 1;
        when(imageRepository.findById(id)).thenReturn(Optional.of(new Image(id, "test.png", "https://test.com", 0.0f)));

        // when
        underTest.deleteImage(id);

        // then
        verify(imageRepository).deleteById(id);
        verify(s3Repository).deleteObject(IMAGES_FOLDER + "/" + "test.png");
    }

    @Test
    void deleteById_throwsException_whenUserToDeleteDoesNotExistWithId() {
        // given
        long id = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteImage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(id));
    }
}