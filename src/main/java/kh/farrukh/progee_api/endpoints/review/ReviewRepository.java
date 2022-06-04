package kh.farrukh.progee_api.endpoints.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByLanguage_Id(long languageId);
}
