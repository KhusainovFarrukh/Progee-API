package kh.farrukh.progee_api.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static kh.farrukh.progee_api.image.ImageConstants.ENDPOINT_IMAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ImageControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ImageRepository imageRepository;

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
}