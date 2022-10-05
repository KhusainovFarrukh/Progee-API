package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageServiceImpl underTest;

    @Test
    void addImage_canAddImage_whenFileIsValid() throws IOException {
        // given
        MultipartFile mockFile = new MockMultipartFile("test.png", new byte[]{});

        // when
        underTest.addImage(mockFile);

        // then
        ArgumentCaptor<Image> imageArgCaptor = ArgumentCaptor.forClass(Image.class);
        verify(imageRepository).save(imageArgCaptor.capture());

        Image actual = imageArgCaptor.getValue();
        assertThat(actual.getContent()).isEqualTo(mockFile.getBytes());
    }

    @Test
    void addImage_throwsException_whenFileIsNull() {
        // given
        MultipartFile mockFile = null;

        // when
        // then
        assertThatThrownBy(() -> underTest.addImage(mockFile))
                .isInstanceOf(RuntimeException.class);
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
    void downloadImage_canDownloadImage_whenIdIsValid() {
        // given
        long id = 1;
        Image existingImage = new Image(id, new byte[]{});
        when(imageRepository.findById(id)).thenReturn(Optional.of(existingImage));

        // when
        Resource actual = underTest.downloadImage(id);

        // then
        assertThat(actual).isEqualTo(new ByteArrayResource(existingImage.getContent()));
    }

    @Test
    void downloadImage_throwsException_whenImageToDownloadDoesNotExist() {
        // given
        long id = 1;
        when(imageRepository.findById(id)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.downloadImage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }
}