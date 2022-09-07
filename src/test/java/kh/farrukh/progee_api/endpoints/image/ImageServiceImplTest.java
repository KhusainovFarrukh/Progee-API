package kh.farrukh.progee_api.endpoints.image;

import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SecurityTestExecutionListeners
class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;
    @InjectMocks
    private ImageServiceImpl underTest;

    @Test
    void canAddImage() throws IOException {
        // given
        MultipartFile multipartImage = new MockMultipartFile("test.png", new byte[]{});

        // when
        underTest.addImage(multipartImage);

        // then
        ArgumentCaptor<Image> imageArgCaptor = ArgumentCaptor.forClass(Image.class);
        verify(imageRepository).save(imageArgCaptor.capture());

        Image capturedImage = imageArgCaptor.getValue();
        assertThat(capturedImage.getContent()).isEqualTo(multipartImage.getBytes());
    }

    @Test
    void throwsExceptionIfFileIsNull() {
        // given
        MultipartFile multipartImage = null;

        // when
        // then
        assertThatThrownBy(() -> underTest.addImage(multipartImage))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void canGetImageById() {
        // given
        long id = 1;
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        // when
        underTest.getImageById(id);

        // then
        verify(imageRepository).findById(id);
    }

    @Test
    void throwsExceptionIfImageDoesNotExistWithId() {
        // given
        long id = 1;
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getImageById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    void canDownloadImage() {
        // given
        Image existingImage = new Image(1, new byte[]{});
        when(imageRepository.findById(any())).thenReturn(Optional.of(existingImage));

        // when
        Resource actual = underTest.downloadImage(existingImage.getId());

        // then
        assertThat(actual).isEqualTo(new ByteArrayResource(existingImage.getContent()));
    }

    @Test
    void throwsExceptionIfImageToDownloadDoesNotExist() {
        // given
        long imageId = 1;
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.downloadImage(imageId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(imageId));
    }
}