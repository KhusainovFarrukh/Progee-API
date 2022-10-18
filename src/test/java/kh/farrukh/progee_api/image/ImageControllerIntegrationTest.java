package kh.farrukh.progee_api.image;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static kh.farrukh.progee_api.image.ImageConstants.ENDPOINT_IMAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ImageControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImageRepository imageRepository;

    @Test
    @WithAnonymousUser
    void getImages_canGetAllImages() throws Exception {
        // given
        List<Image> images = imageRepository.saveAll(List.of(new Image(), new Image(), new Image()));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_IMAGE).param("page_size", String.valueOf(images.size())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<ImageResponseDTO> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(actual.getTotalItems()).isEqualTo(images.size());
        List<Long> expectedIds = images.stream().map(Image::getId).toList();
        assertThat(actual.getItems().stream().allMatch(imageDTO -> expectedIds.contains(imageDTO.getId()))).isTrue();
    }

    @Test
    @WithAnonymousUser
    void getImageById_canGetImageById() throws Exception {
        // given
        Image existingImage = imageRepository.save(new Image());

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_IMAGE + "/" + existingImage.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        ImageResponseDTO actual = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ImageResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingImage.getId());
    }

    @Test
    @WithAnonymousUser
    @Disabled
    void uploadImage_canUploadImage() throws Exception {
        // given
        byte[] bytes = "test".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile("image", bytes);

        // when
        mvc.perform(multipart(ENDPOINT_IMAGE).file(mockFile))
                .andDo(print())
                .andExpect(status().isCreated());

        // then
        assertThat(imageRepository.findAll().stream().anyMatch(
                image -> image.getName().contains(mockFile.getName()))
        ).isTrue();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    @Disabled
    void deleteImage_canDeleteImage() {

    }
}