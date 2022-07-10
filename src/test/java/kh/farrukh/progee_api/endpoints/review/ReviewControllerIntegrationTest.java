package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.test_utils.ReviewValueDeserializer;
import kh.farrukh.progee_api.test_utils.ReviewValueSerializer;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LANGUAGE;
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
    private UserRepository userRepository;

    @Autowired
    private ReviewService reviewService;

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
        userRepository.deleteAll();
    }

    @Test
    void canGetReviewsWithoutFilter() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        List<Review> reviews = List.of(
                new Review("test body1", ReviewValue.LIKE, existingLanguage),
                new Review("test body2", ReviewValue.LIKE, existingLanguage),
                new Review("test body3", ReviewValue.LIKE, existingLanguage)
        );
        reviewRepository.saveAll(reviews);

        // when
        MvcResult result = mvc
                .perform(
                        get(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/reviews")
                                .param("page_size", String.valueOf(reviews.size()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<Review> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(reviews.size());
        assertThat(reviews.stream().allMatch(review ->
                response.getItems().stream().map(Review::getBody).collect(Collectors.toList())
                        .contains(review.getBody())
        )).isTrue();
    }

    @Test
    void canGetFrameworksWithFilter() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        List<Review> likeReviews = List.of(
                new Review("test body1", ReviewValue.LIKE, existingLanguage),
                new Review("test body2", ReviewValue.LIKE, existingLanguage)
        );
        List<Review> dislikeReviews = List.of(
                new Review("test body3", ReviewValue.DISLIKE, existingLanguage)
        );
        reviewRepository.saveAll(likeReviews);

        // when
        MvcResult result = mvc
                .perform(
                        get(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/reviews")
                                .param("page_size", String.valueOf(likeReviews.size() + dislikeReviews.size()))
                                .param("state", String.valueOf(ReviewValue.LIKE.getScore()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<Review> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(likeReviews.size());
        assertThat(likeReviews.stream().allMatch(review ->
                response.getItems().stream().map(Review::getBody).collect(Collectors.toList())
                        .contains(review.getBody())
        )).isTrue();
    }

    @Test
    void canGetReviewById() throws Exception {
        // given
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewRepository.save(
                new Review("test body", ReviewValue.LIKE, existingLanguage)
        );

        // when
        MvcResult result = mvc
                .perform(get(ENDPOINT_LANGUAGE + "/"
                        + existingLanguage.getId() +
                        "/reviews/" + existingReview.getId()
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Review review = objectMapper.readValue(result.getResponse().getContentAsString(), Review.class);
        assertThat(review.getId()).isEqualTo(existingReview.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canAddReview() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser("user@mail.com", UserRole.USER));
        Language existingLanguage = languageRepository.save(new Language());
        ReviewDTO reviewDto = new ReviewDTO("test body", ReviewValue.LIKE);

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        Review review = objectMapper.readValue(result.getResponse().getContentAsString(), Review.class);
        assertThat(review.getBody()).isEqualTo(reviewDto.getBody());
        assertThat(review.getReviewValue()).isEqualTo(reviewDto.getValue());
        assertThat(review.getAuthor().getId()).isEqualTo(existingUser.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canUpdateReview() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser("user@mail.com", UserRole.USER));
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewService.addReview(
                existingLanguage.getId(), new ReviewDTO("test body", ReviewValue.LIKE)
        );
        ReviewDTO reviewDto = new ReviewDTO("test body update", ReviewValue.LIKE);

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_LANGUAGE + "/" + existingLanguage.getId() + "/reviews/" + existingReview.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Review review = objectMapper.readValue(result.getResponse().getContentAsString(), Review.class);
        assertThat(review.getId()).isEqualTo(existingReview.getId());
        assertThat(review.getBody()).isEqualTo(reviewDto.getBody());
        assertThat(review.getReviewValue()).isEqualTo(reviewDto.getValue());
        assertThat(review.getAuthor().getId()).isEqualTo(existingUser.getId());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", authorities = "ADMIN")
    void canDeleteReviewById() throws Exception {
        // given
        userRepository.save(new AppUser("admin@mail.com", UserRole.ADMIN));
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewService.addReview(
                existingLanguage.getId(), new ReviewDTO("test body", ReviewValue.LIKE)
        );

        // when
        // then
        mvc.perform(delete(ENDPOINT_LANGUAGE + "/"
                        + existingLanguage.getId()
                        + "/reviews/" + existingReview.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canVoteReview() throws Exception {
        // given
        AppUser existingUser = userRepository.save(new AppUser("user@mail.com", UserRole.USER));
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewRepository.save(new Review("", ReviewValue.LIKE, existingLanguage));
        ReviewVoteDTO voteDTO = new ReviewVoteDTO(true);

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_LANGUAGE + "/"
                        + existingLanguage.getId() + "/reviews/"
                        + existingReview.getId() + "/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Review review = objectMapper.readValue(result.getResponse().getContentAsString(), Review.class);
        assertThat(review.getId()).isEqualTo(existingReview.getId());
        assertThat(existingUser.getId()).isIn(review.getUpVotes());
        assertThat(existingUser.getId()).isNotIn(review.getDownVotes());
    }
}