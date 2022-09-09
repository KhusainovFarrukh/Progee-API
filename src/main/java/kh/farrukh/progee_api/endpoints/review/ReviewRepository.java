package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.utils.paging_sorting.AllowedSortFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

/**
 * Repository for managing frameworks
 */
@Repository
@Validated
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    /**
     * "Find all reviews for a given language, and return them in a pageable format."
     * <p>
     * The @AllowedSortFields annotation is a custom annotation that I created to ensure that the user can only sort by the
     * fields that I want them to
     *
     * @param languageId the id of the language to filter by
     * @param pageable   the pageable object that contains the page number, page size, and sort information.
     * @return A Page of Reviews.
     */
    Page<Review> findByLanguage_Id(
            long languageId,
            // TODO: 6/12/22 add custom (transient) field: score
            @AllowedSortFields({"id", "body", "reviewValue", "upVotes", "downVotes", "createdAt"}) Pageable pageable
    );

    /**
     * "Find all reviews with given reviewValue for a given language, and return them in a pageable format."
     * <p>
     * The @AllowedSortFields annotation is a custom annotation that I created to ensure that the user can only sort by the
     * fields that I want them to
     *
     * @param languageId the id of the language to filter by
     * @param value      ReviewValue to filter by (optional).
     * @param pageable   the pageable object that contains the page number, page size, and sort information.
     * @return A Page of Reviews.
     */
    Page<Review> findByLanguage_IdAndReviewValue(
            long languageId,
            ReviewValue value,
            // TODO: 6/12/22 add custom (transient) field: score
            @AllowedSortFields({"id", "body", "reviewValue", "upVotes", "downVotes", "createdAt"}) Pageable pageable
    );
}
