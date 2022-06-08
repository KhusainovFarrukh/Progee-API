package kh.farrukh.progee_api.endpoints.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByLanguage_Id(long languageId);

    Page<Review> findByLanguage_Id(long languageId, Pageable pageable);
}
