package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.AllowedSortFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

/**
 * Repository for managing frameworks
 */
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
}
