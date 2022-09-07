package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.AllowedSortFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

/**
 * Repository for managing frameworks
 */
@Repository
@Validated
public interface LanguageRepository extends JpaRepository<Language, Long> {

    /**
     * Returns true if there is a language with the given name.
     *
     * @param name The name of the language to check for.
     * @return A boolean value.
     */
    boolean existsByName(String name);

    /**
     * "Find all languages with the given state, and return them in the given pageable."
     *
     * The @AllowedSortFields annotation is a custom annotation that I created to ensure that the user can only sort by the
     * fields that I want them to
     *
     * @param state The state of the resource.
     * @param pageable The pageable object that contains the page number, page size, and sort information.
     * @return A Page of Language objects.
     */
    Page<Language> findByState(
            ResourceState state,
            @AllowedSortFields({"id", "name", "description", "state", "createdAt"}) Pageable pageable
    );

    @EntityGraph(value = "language_with_frameworks", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select l from Language l")
    Page<Language> findAllWithFrameworks(Pageable pageable);

    @EntityGraph(value = "language_with_reviews", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select l from Language l")
    Page<Language> findAllWithReviews(Pageable pageable);
}
