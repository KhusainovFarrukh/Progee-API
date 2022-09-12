package kh.farrukh.progee_api.review;

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
}
