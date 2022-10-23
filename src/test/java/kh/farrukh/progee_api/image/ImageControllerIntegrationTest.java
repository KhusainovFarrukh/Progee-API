package kh.farrukh.progee_api.image;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static kh.farrukh.progee_api.image.ImageConstants.ENDPOINT_IMAGE;
import static kh.farrukh.progee_api.image.ImageServiceImpl.IMAGES_FOLDER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * These tests use LocalStack to mock AWS S3.
 * Configure and start your Docker daemon before running these tests.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class ImageControllerIntegrationTest {

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:1.2.0")
    ).withServices(LocalStackContainer.Service.S3);

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AmazonS3Client s3Client;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ImageService imageService;

    @AfterEach
    void tearDown() {
        imageRepository.deleteAll();
    }

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
    void uploadImage_canUploadImage() throws Exception {
        // given
        byte[] bytes = "test".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile("image", "image.png", "image/png", bytes);

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
    void deleteImage_canDeleteImage() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_DELETE_IMAGE)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));

        byte[] bytes = "test".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile("image", "image.png", "image/png", bytes);
        ImageResponseDTO imageResponseDTO = imageService.addImage(mockFile);

        // when
        mvc.perform(delete(ENDPOINT_IMAGE + "/" + imageResponseDTO.getId())
                        .header("Authorization", "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isNoContent());

        // then
        assertThat(imageRepository.findById(imageResponseDTO.getId())).isEmpty();
        assertThat(s3Client.doesObjectExist(bucketName, IMAGES_FOLDER + "/" + imageResponseDTO.getName())).isFalse();
    }

    /**
     * It creates a bean that is an AmazonS3Client that is configured to use the localstack S3 service
     */
    @TestConfiguration
    static class AmazonS3Configuration {

        @Bean
        @Primary
        public AmazonS3Client amazonS3Client(
                @Value("${aws.s3.bucket}") String bucketName,
                @Value("${cloud.aws.region.static}") String region
        ) {
            AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                            localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString(),
                            region
                    ))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                            localStack.getAccessKey(),
                            localStack.getSecretKey()
                    )))
                    .build();
            s3.createBucket(bucketName);
            return (AmazonS3Client) s3;
        }
    }
}