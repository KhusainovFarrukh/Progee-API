package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.utils.sorting.AllowedSortFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByLanguage_Id(
            long languageId,
            // TODO: 6/12/22 add custom (transient) field: score
            @AllowedSortFields({"id", "body", "value", "upVotes", "downVotes", "createdAt"}) Pageable pageable
    );
}
