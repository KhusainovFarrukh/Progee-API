package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ReviewSpecificationIntegrationTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @AfterEach
    void tearDown() {
        languageRepository.deleteAll();
        reviewRepository.deleteAll();
    }

    @Test
    void repository_returnsValidData_whenLanguageIdIsNullAndReviewValueIsNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        List<Review> reviews = reviewRepository.saveAll(List.of(
                new Review("Spring", ReviewValue.LIKE, languages.get(0)),
                new Review("Django", ReviewValue.DISLIKE, languages.get(1)),
                new Review("React", ReviewValue.WANT_TO_LEARN, languages.get(2)),
                new Review("Angular", ReviewValue.LIKE, languages.get(2)),
                new Review("Vue", ReviewValue.DONT_HAVE_PRACTICE, languages.get(2))
        ));
        ReviewSpecification reviewSpecification = new ReviewSpecification(null, null);

        // when
        List<Review> actual = reviewRepository.findAll(reviewSpecification);

        // then
        assertThat(actual.size()).isEqualTo(reviews.size());
    }

    @Test
    void repository_returnsValidData_whenLanguageIdIsNotNullAndReviewValueIsNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        reviewRepository.saveAll(List.of(
                new Review("Spring", ReviewValue.LIKE, languages.get(0))
        ));
        reviewRepository.saveAll(List.of(
                new Review("Django", ReviewValue.DISLIKE, languages.get(1))
        ));
        List<Review> language2reviews = reviewRepository.saveAll(List.of(
                new Review("React", ReviewValue.WANT_TO_LEARN, languages.get(2)),
                new Review("Angular", ReviewValue.LIKE, languages.get(2)),
                new Review("Vue", ReviewValue.DONT_HAVE_PRACTICE, languages.get(2))
        ));
        ReviewSpecification reviewSpecification = new ReviewSpecification(languages.get(2).getId(), null);

        // when
        List<Review> actual = reviewRepository.findAll(reviewSpecification);

        // then
        assertThat(actual.size()).isEqualTo(language2reviews.size());
        List<Long> expectedIds = language2reviews.stream().map(Review::getId).toList();
        assertThat(actual.stream().allMatch(review -> expectedIds.contains(review.getId()))).isTrue();
    }

    @Test
    void repository_returnsValidData_whenLanguageIdIsNullAndReviewValueIsNotNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        List<Review> likeReviews = reviewRepository.saveAll(List.of(
                new Review("Spring", ReviewValue.LIKE, languages.get(0)),
                new Review("Angular", ReviewValue.LIKE, languages.get(2))
        ));
        reviewRepository.saveAll(List.of(
                new Review("Django", ReviewValue.DISLIKE, languages.get(1))
        ));
        reviewRepository.saveAll(List.of(
                new Review("React", ReviewValue.WANT_TO_LEARN, languages.get(2))
        ));
        reviewRepository.saveAll(List.of(
                new Review("Vue", ReviewValue.DONT_HAVE_PRACTICE, languages.get(2))
        ));
        ReviewSpecification reviewSpecification = new ReviewSpecification(null, ReviewValue.LIKE);

        // when
        List<Review> actual = reviewRepository.findAll(reviewSpecification);

        // then
        assertThat(actual.size()).isEqualTo(likeReviews.size());
        List<Long> expectedIds = likeReviews.stream().map(Review::getId).toList();
        assertThat(actual.stream().allMatch(review -> expectedIds.contains(review.getId()))).isTrue();
    }

    @Test
    void repository_returnsValidData_whenLanguageIdIsNotNullAndReviewValueIsNotNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        reviewRepository.saveAll(List.of(
                new Review("Spring", ReviewValue.LIKE, languages.get(0))
        ));
        reviewRepository.saveAll(List.of(
                new Review("Django", ReviewValue.DISLIKE, languages.get(1))
        ));
        List<Review> language2LikeReviews = reviewRepository.saveAll(List.of(
                new Review("Angular", ReviewValue.LIKE, languages.get(2)),
                new Review("Vue", ReviewValue.LIKE, languages.get(2))
        ));
        reviewRepository.saveAll(List.of(
                new Review("React", ReviewValue.WANT_TO_LEARN, languages.get(2))
        ));
        reviewRepository.saveAll(List.of(
                new Review("Vue", ReviewValue.DONT_HAVE_PRACTICE, languages.get(2))
        ));

        ReviewSpecification reviewSpecification = new ReviewSpecification(languages.get(2).getId(), ReviewValue.LIKE);

        // when
        List<Review> actual = reviewRepository.findAll(reviewSpecification);

        // then
        assertThat(actual.size()).isEqualTo(language2LikeReviews.size());
        List<Long> expectedIds = language2LikeReviews.stream().map(Review::getId).toList();
        assertThat(actual.stream().allMatch(review -> expectedIds.contains(review.getId()))).isTrue();
    }

    @Test
    void equals_returnsTrue_whenBothLanguageIdAndReviewValueAreNull() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(null, null);
        ReviewSpecification reviewSpecification2 = new ReviewSpecification(null, null);

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsTrue_whenBothLanguageIdAndReviewValueAreEqual() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(1L, ReviewValue.LIKE);
        ReviewSpecification reviewSpecification2 = new ReviewSpecification(1L, ReviewValue.LIKE);

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsFalse_whenLanguageIdIsNotEqual() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(1L, ReviewValue.LIKE);
        ReviewSpecification reviewSpecification2 = new ReviewSpecification(2L, ReviewValue.LIKE);

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void equals_returnsFalse_whenReviewValueIsNotEqual() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(1L, ReviewValue.LIKE);
        ReviewSpecification reviewSpecification2 = new ReviewSpecification(1L, ReviewValue.DISLIKE);

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void equals_returnsTrue_whenSameObject() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(1L, ReviewValue.LIKE);
        ReviewSpecification reviewSpecification2 = reviewSpecification1;

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsFalse_whenObjectIsNull() {
        // given
        ReviewSpecification reviewSpecification = new ReviewSpecification(1L, ReviewValue.LIKE);

        // when
        boolean actual = reviewSpecification.equals(null);

        // then
        assertThat(actual).isFalse();
    }
}