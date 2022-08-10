package kh.farrukh.progee_api.endpoints.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing images
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
