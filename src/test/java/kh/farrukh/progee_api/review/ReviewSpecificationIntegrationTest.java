package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
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
    void returnsValidDataIfLanguageIdIsNullAndReviewValueIsNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        List<Review> reviews = List.of(
                new Review("Spring", ReviewValue.LIKE, languages.get(0)),
                new Review("Django", ReviewValue.DISLIKE, languages.get(1)),
                new Review("React", ReviewValue.WANT_TO_LEARN, languages.get(2)),
                new Review("Angular", ReviewValue.LIKE, languages.get(2)),
                new Review("Vue", ReviewValue.DONT_HAVE_PRACTICE, languages.get(2))
        );
        reviews = reviewRepository.saveAll(reviews);
        ReviewSpecification reviewSpecification = new ReviewSpecification(null, null);

        // when
        List<Review> actual = reviewRepository.findAll(reviewSpecification);

        // then
        assertThat(actual.size()).isEqualTo(reviews.size());
    }

    @Test
    void returnsValidDataIfLanguageIdIsNotNullAndReviewValueIsNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        List<Review> reviews = List.of(
                new Review("Spring", ReviewValue.LIKE, languages.get(0)),
                new Review("Django", ReviewValue.DISLIKE, languages.get(1)),
                new Review("React", ReviewValue.WANT_TO_LEARN, languages.get(2)),
                new Review("Angular", ReviewValue.LIKE, languages.get(2)),
                new Review("Vue", ReviewValue.DONT_HAVE_PRACTICE, languages.get(2))
        );
        reviews = reviewRepository.saveAll(reviews);
        ReviewSpecification reviewSpecification = new ReviewSpecification(languages.get(2).getId(), null);

        // when
        List<Review> actual = reviewRepository.findAll(reviewSpecification);

        // then
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    void returnsValidDataIfLanguageIdIsNullAndReviewValueIsNotNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        List<Review> reviews = List.of(
                new Review("Spring", ReviewValue.LIKE, languages.get(0)),
                new Review("Django", ReviewValue.DISLIKE, languages.get(1)),
                new Review("React", ReviewValue.WANT_TO_LEARN, languages.get(2)),
                new Review("Angular", ReviewValue.LIKE, languages.get(2)),
                new Review("Vue", ReviewValue.DONT_HAVE_PRACTICE, languages.get(2))
        );
        reviews = reviewRepository.saveAll(reviews);
        ReviewSpecification reviewSpecification = new ReviewSpecification(null, ReviewValue.LIKE);

        // when
        List<Review> actual = reviewRepository.findAll(reviewSpecification);

        // then
        assertThat(actual.size()).isEqualTo(2);
    }

    @Test
    void returnsValidDataIfLanguageIdIsNotNullAndReviewValueIsNotNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        List<Review> reviews = List.of(
                new Review("Spring", ReviewValue.LIKE, languages.get(0)),
                new Review("Django", ReviewValue.DISLIKE, languages.get(1)),
                new Review("React", ReviewValue.WANT_TO_LEARN, languages.get(2)),
                new Review("Angular", ReviewValue.LIKE, languages.get(2)),
                new Review("Vue", ReviewValue.DONT_HAVE_PRACTICE, languages.get(2))
        );
        reviews = reviewRepository.saveAll(reviews);
        ReviewSpecification reviewSpecification = new ReviewSpecification(languages.get(2).getId(), ReviewValue.LIKE);

        // when
        List<Review> actual = reviewRepository.findAll(reviewSpecification);

        // then
        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    void returnsTrueIfBothLanguageIdAndStateAreNull() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(null, null);
        ReviewSpecification reviewSpecification2 = new ReviewSpecification(null, null);

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsTrueIfBothLanguageIdAndReviewValueAreEqual() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(1L, ReviewValue.LIKE);
        ReviewSpecification reviewSpecification2 = new ReviewSpecification(1L, ReviewValue.LIKE);

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsFalseIfLanguageIdIsNotEqual() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(1L, ReviewValue.LIKE);
        ReviewSpecification reviewSpecification2 = new ReviewSpecification(2L, ReviewValue.LIKE);

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void returnsFalseIfReviewValueIsNotEqual() {
        // given
        ReviewSpecification reviewSpecification1 = new ReviewSpecification(1L, ReviewValue.LIKE);
        ReviewSpecification reviewSpecification2 = new ReviewSpecification(1L, ReviewValue.DISLIKE);

        // when
        boolean actual = reviewSpecification1.equals(reviewSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void returnsTrueIfSameObject() {
        // given
        ReviewSpecification reviewSpecification = new ReviewSpecification(1L, ReviewValue.LIKE);

        // when
        boolean actual = reviewSpecification.equals(reviewSpecification);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsFalseIfObjectIsNull() {
        // given
        ReviewSpecification reviewSpecification = new ReviewSpecification(1L, ReviewValue.LIKE);

        // when
        boolean actual = reviewSpecification.equals(null);

        // then
        assertThat(actual).isFalse();
    }
}