package kh.farrukh.progee_api.review;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.review.payloads.ReviewRequestDTO;
import kh.farrukh.progee_api.review.payloads.ReviewResponseDTO;
import kh.farrukh.progee_api.review.payloads.ReviewVoteRequestDTO;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.test_utils.ReviewValueDeserializer;
import kh.farrukh.progee_api.test_utils.ReviewValueSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static kh.farrukh.progee_api.review.ReviewConstants.ENDPOINT_REVIEW;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ReviewValue.class, new ReviewValueSerializer());
        module.addDeserializer(ReviewValue.class, new ReviewValueDeserializer());
        objectMapper.registerModule(module);
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        languageRepository.deleteAll();
        appUserRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void getReviews_canGetReviewsWithoutFilter() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        List<Review> reviews = reviewRepository.saveAll(List.of(
                new Review("test body1", ReviewValue.LIKE, existingLanguage),
                new Review("test body2", ReviewValue.LIKE, existingLanguage),
                new Review("test body3", ReviewValue.LIKE, existingLanguage)
        ));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_REVIEW)
                        .param("page_size", String.valueOf(reviews.size())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<ReviewResponseDTO> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(actual.getTotalItems()).isEqualTo(reviews.size());
        List<Long> expectedIds = reviews.stream().map(Review::getId).toList();
        assertThat(actual.getItems().stream().allMatch(review -> expectedIds.contains(review.getId()))).isTrue();
    }

    @Test
    @WithAnonymousUser
    void getReviews_canGetReviewsWithFilter() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        List<Review> likeReviews = reviewRepository.saveAll(List.of(
                new Review("test body1", ReviewValue.LIKE, existingLanguage),
                new Review("test body2", ReviewValue.LIKE, existingLanguage)
        ));
        List<Review> dislikeReviews = reviewRepository.saveAll(List.of(
                new Review("test body3", ReviewValue.DISLIKE, existingLanguage)
        ));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_REVIEW)
                        .param("page_size", String.valueOf(likeReviews.size() + dislikeReviews.size()))
                        .param("value", ReviewValue.LIKE.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<ReviewResponseDTO> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(actual.getTotalItems()).isEqualTo(likeReviews.size());
        List<Long> expectedIds = likeReviews.stream().map(Review::getId).toList();
        assertThat(actual.getItems().stream().allMatch(review -> expectedIds.contains(review.getId()))).isTrue();
    }

    @Test
    @WithAnonymousUser
    void getReviewById_canGetReviewById() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewRepository.save(new Review("test body", ReviewValue.LIKE, existingLanguage));

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_REVIEW + "/" + existingReview.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        ReviewResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), ReviewResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingReview.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void addReview_canAddReview() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_CREATE_REVIEW)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO("test body", ReviewValue.LIKE, existingLanguage.getId());

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_REVIEW)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        ReviewResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), ReviewResponseDTO.class);
        assertThat(actual.getBody()).isEqualTo(reviewRequestDto.getBody());
        assertThat(actual.getReviewValue()).isEqualTo(reviewRequestDto.getReviewValue());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void updateReview_canUpdateReview() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_REVIEW)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        ReviewResponseDTO existingReview = reviewService.addReview(
                new ReviewRequestDTO("test body", ReviewValue.LIKE, existingLanguage.getId())
        );
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO("test body update", ReviewValue.LIKE);

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_REVIEW + "/" + existingReview.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        ReviewResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), ReviewResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingReview.getId());
        assertThat(actual.getBody()).isEqualTo(reviewRequestDto.getBody());
        assertThat(actual.getReviewValue()).isEqualTo(reviewRequestDto.getReviewValue());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void deleteReview_canDeleteReviewById() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_DELETE_OWN_REVIEW)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        ReviewResponseDTO existingReview = reviewService.addReview(
                new ReviewRequestDTO("test body", ReviewValue.LIKE, existingLanguage.getId())
        );

        // when
        // then
        mvc.perform(delete(ENDPOINT_REVIEW + "/" + existingReview.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(reviewRepository.findById(existingReview.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void voteReview_canVoteReview() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VOTE_REVIEW)));
        AppUser existingUser = appUserRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewRepository.save(new Review("", ReviewValue.LIKE, existingLanguage));
        ReviewVoteRequestDTO voteDTO = new ReviewVoteRequestDTO(true);

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_REVIEW + "/" + existingReview.getId() + "/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteDTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(
                                existingUser, ZonedDateTime.now().plusSeconds(tokenProvider.getJwtConfiguration().getAccessTokenValidityInSeconds())
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        ReviewResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), ReviewResponseDTO.class);
        assertThat(actual.getId()).isEqualTo(existingReview.getId());
        assertThat(existingUser.getId()).isIn(actual.getUpVotes());
        assertThat(existingUser.getId()).isNotIn(actual.getDownVotes());
    }
}