package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

/**
 * Repository for managing languages
 */
@Repository
@Validated
public interface LanguageRepository extends JpaRepository<Language, Long>, JpaSpecificationExecutor<Language> {

    /**
     * Returns true if there is a language with the given name.
     *
     * @param name The name of the language to check for.
     * @return A boolean value.
     */
    boolean existsByName(String name);

    /**
     * "Find all languages with the given state, and return them in the given pageable."
     * <p>
     *
     * @param state    The state of the resource.
     * @param pageable The pageable object that contains the page number, page size, and sort information.
     * @return A Page of Language objects.
     */
    Page<Language> findByState(
            ResourceState state,
            Pageable pageable
    );

    /**
     * Find all languages, and load their frameworks.
     *
     * @param pageable This is the pageable object that contains the page number, page size, and sort order.
     * @return A Page of Language objects with the frameworks loaded.
     */
    @EntityGraph(value = "language_with_frameworks", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select l from Language l")
    Page<Language> findAllWithFrameworks(Pageable pageable);

    /**
     * Find all languages, and load their reviews.
     *
     * @param pageable This is the Pageable object that we created in the controller.
     * @return A Page of Language objects with their reviews loaded.
     */
    @EntityGraph(value = "language_with_reviews", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select l from Language l")
    Page<Language> findAllWithReviews(Pageable pageable);
}
