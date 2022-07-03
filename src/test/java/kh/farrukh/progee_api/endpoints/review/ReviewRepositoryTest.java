package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository underTest;
    @Autowired
    private LanguageRepository languageRepository;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void returnValidDataIfContainsReviewsOfOnlyQueriedLanguage() {
        // given
        Language language = languageRepository.save(new Language());

        Review review1 = new Review(language);
        Review review2 = new Review(language);
        Review review3 = new Review(language);

        List<Review> reviews = List.of(review1, review2, review3);
        underTest.saveAll(reviews);

        // when
        Page<Review> pagedData = underTest.findByLanguage_Id(language.getId(), PageRequest.of(0, reviews.size()));

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(reviews.size());
        assertThat(pagedData.map(
                (review) -> review.getLanguage().getId() == language.getId()
        ).getContent().size()).isEqualTo(reviews.size());
    }

    @Test
    void returnValidDataIfContainsReviewsOfMultipleLanguages() {
        // given
        Language language1 = languageRepository.save(new Language());
        Language language2 = languageRepository.save(new Language());

        Review review1 = new Review(language1);
        Review review2 = new Review(language1);
        Review review3 = new Review(language2);

        List<Review> language1Reviews = List.of(review1, review2);
        List<Review> language2Reviews = List.of(review3);
        underTest.saveAll(language1Reviews);
        underTest.saveAll(language2Reviews);

        // when
        Page<Review> pagedData = underTest.findByLanguage_Id(
                language1.getId(),
                PageRequest.of(
                        0,
                        language1Reviews.size() + language2Reviews.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(language1Reviews.size());
        assertThat(pagedData.map(
                (review) -> review.getLanguage().getId() == language1.getId()
        ).getContent().size()).isEqualTo(language1Reviews.size());
    }

    @Test
    void returnEmptyDataIfContainsReviewsOfOnlyOtherLanguages() {
        // given
        Language language1 = languageRepository.save(new Language());
        Language language2 = languageRepository.save(new Language());

        Review review1 = new Review(language1);
        Review review2 = new Review(language1);
        Review review3 = new Review(language1);

        List<Review> language1Reviews = List.of(review1, review2, review3);
        underTest.saveAll(language1Reviews);

        // when
        Page<Review> pagedData = underTest.findByLanguage_Id(
                language2.getId(),
                PageRequest.of(
                        0,
                        language1Reviews.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (review) -> review.getLanguage().getId() == language2.getId()
        ).getContent().size()).isEqualTo(0);
    }

    @Test
    void returnEmptyDataIfDoesNotContainAnyReview() {
        // given
        Language language = languageRepository.save(new Language());

        // when
        Page<Review> pagedData = underTest.findByLanguage_Id(
                language.getId(),
                PageRequest.of(0, 10)
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (review) -> review.getLanguage().getId() == language.getId()
        ).getContent().size()).isEqualTo(0);
    }

    @Test
    void returnsValidDataIfContainsOnlyOneValueReviews() {
        // given
        Language language = languageRepository.save(new Language());
        ReviewValue value = ReviewValue.LIKE;

        Review review1 = new Review(value, language);
        Review review2 = new Review(value, language);
        Review review3 = new Review(value, language);

        List<Review> reviews = List.of(review1, review2, review3);
        underTest.saveAll(reviews);

        // when
        Page<Review> pagedData = underTest.findByLanguage_IdAndReviewValue(
                language.getId(),
                value,
                PageRequest.of(0, reviews.size())
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(reviews.size());
        assertThat(pagedData.map(
                (review) -> review.getReviewValue() == value
        ).getContent().size()).isEqualTo(reviews.size());
    }

    @Test
    void returnsValidDataIfContainsMultipleStateFrameworks() {
        // given
        Language language = languageRepository.save(new Language());

        ReviewValue value1 = ReviewValue.LIKE;
        ReviewValue value2 = ReviewValue.DISLIKE;
        ReviewValue value3 = ReviewValue.WANT_TO_LEARN;

        Review review1 = new Review(value1, language);
        Review review2 = new Review(value1, language);
        Review review3 = new Review(value2, language);
        Review review4 = new Review(value2, language);
        Review review5 = new Review(value3, language);

        List<Review> value1Reviews = List.of(review1, review2);
        List<Review> value2Reviews = List.of(review3, review4);
        List<Review> value3Reviews = List.of(review5);
        underTest.saveAll(value1Reviews);
        underTest.saveAll(value2Reviews);
        underTest.saveAll(value3Reviews);

        // when
        Page<Review> pagedData = underTest.findByLanguage_IdAndReviewValue(
                language.getId(),
                value1,
                PageRequest.of(
                        0,
                        value1Reviews.size() + value2Reviews.size() + value3Reviews.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(value1Reviews.size());
        assertThat(pagedData.map(
                (review) -> review.getReviewValue() == value1
        ).getContent().size()).isEqualTo(value1Reviews.size());
    }

    @Test
    void returnsEmptyDataIfContainsOnlyOtherValueReviews() {
        // given
        Language language = languageRepository.save(new Language());

        ReviewValue value1 = ReviewValue.LIKE;
        ReviewValue value2 = ReviewValue.DISLIKE;
        ReviewValue value3 = ReviewValue.WANT_TO_LEARN;

        Review review1 = new Review(value1, language);
        Review review2 = new Review(value1, language);
        Review review3 = new Review(value2, language);
        Review review4 = new Review(value2, language);
        Review review5 = new Review(value2, language);

        List<Review> value1Reviews = List.of(review1, review2);
        List<Review> value2Reviews = List.of(review3, review4, review5);
        underTest.saveAll(value1Reviews);
        underTest.saveAll(value2Reviews);

        // when
        Page<Review> pagedData = underTest.findByLanguage_IdAndReviewValue(
                language.getId(),
                value3,
                PageRequest.of(
                        0,
                        value1Reviews.size() + value2Reviews.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (review) -> review.getReviewValue() == value3
        ).getContent().size()).isEqualTo(0);
    }

    @Test
    void returnsEmptyDataIfDoesNotContainAnyReview() {
        // given
        ReviewValue value = ReviewValue.LIKE;

        // when
        Page<Review> pagedData = underTest.findByLanguage_IdAndReviewValue(
                1,
                value,
                PageRequest.of(
                        0,
                        10
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (review) -> review.getReviewValue() == value
        ).getContent().size()).isEqualTo(0);
    }
}