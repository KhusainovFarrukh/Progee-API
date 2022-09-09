package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
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

import java.util.Collections;
import java.util.List;

import static kh.farrukh.progee_api.endpoints.review.ReviewController.ENDPOINT_REVIEW;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
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
    private RoleRepository roleRepository;

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
        roleRepository.deleteAll();
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
                .perform(get(ENDPOINT_REVIEW)
                        .param("page_size", String.valueOf(reviews.size())))
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
                response.getItems().stream().map(Review::getBody).toList()
                        .contains(review.getBody())
        )).isTrue();
    }

    @Test
    void canGetReviewsWithFilter() throws Exception {
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
                        get(ENDPOINT_REVIEW)
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
                response.getItems().stream().map(Review::getBody).toList()
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
                .perform(get(ENDPOINT_REVIEW + "/" + existingReview.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Review review = objectMapper.readValue(result.getResponse().getContentAsString(), Review.class);
        assertThat(review.getId()).isEqualTo(existingReview.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canAddReview() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_CREATE_REVIEW)));
        userRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        ReviewDTO reviewDto = new ReviewDTO("test body", ReviewValue.LIKE, existingLanguage.getId());

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_REVIEW)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        Review review = objectMapper.readValue(result.getResponse().getContentAsString(), Review.class);
        assertThat(review.getBody()).isEqualTo(reviewDto.getBody());
        assertThat(review.getReviewValue()).isEqualTo(reviewDto.getValue());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canUpdateReview() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_REVIEW)));
        userRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewService.addReview(
                new ReviewDTO("test body", ReviewValue.LIKE, existingLanguage.getId())
        );
        ReviewDTO reviewDto = new ReviewDTO("test body update", ReviewValue.LIKE);

        // when
        MvcResult result = mvc
                .perform(put(ENDPOINT_REVIEW + "/" + existingReview.getId())
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
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canDeleteReviewById() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_DELETE_OWN_REVIEW)));
        userRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewService.addReview(
                new ReviewDTO("test body", ReviewValue.LIKE, existingLanguage.getId())
        );

        // when
        // then
        mvc.perform(delete(ENDPOINT_REVIEW + "/" + existingReview.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canVoteReview() throws Exception {
        // given
        Role existingRole = roleRepository.save(new Role(Collections.singletonList(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE)));
        AppUser existingUser = userRepository.save(new AppUser("user@mail.com", existingRole));
        Language existingLanguage = languageRepository.save(new Language());
        Review existingReview = reviewRepository.save(new Review("", ReviewValue.LIKE, existingLanguage));
        ReviewVoteDTO voteDTO = new ReviewVoteDTO(true);

        // when
        MvcResult result = mvc
                .perform(post(ENDPOINT_REVIEW + "/"
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