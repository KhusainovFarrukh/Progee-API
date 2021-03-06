package kh.farrukh.progee_api.endpoints.image;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing images
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
