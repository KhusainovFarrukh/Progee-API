package kh.farrukh.progee_api.endpoints.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_IMAGE;
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
    void canGetImageById() throws Exception {
        // given
        Image existingImage = imageRepository.save(new Image());

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_IMAGE + "/" + existingImage.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Image image = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Image.class);
        assertThat(image.getId()).isEqualTo(image.getId());
    }

    @Test
    void canUploadImage() throws Exception {
        // given
        MockMultipartFile mockImage = new MockMultipartFile("image", "test".getBytes());

        // when
        mvc.perform(multipart(ENDPOINT_IMAGE).file(mockImage))
                .andDo(print())
                .andExpect(status().isCreated());

        // then
        assertThat(imageRepository.findAll().stream().anyMatch(image ->
                Arrays.equals(image.getContent(), "test".getBytes()))).isTrue();
    }

    @Test
    void canDownloadImage() throws Exception {
        // given
        Image existingImage = imageRepository.save(new Image("test".getBytes()));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_IMAGE + "/" + existingImage.getId() + "/download"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        byte[] content = result.getResponse().getContentAsByteArray();
        assertThat(content).isEqualTo(existingImage.getContent());
    }
}