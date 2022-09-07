package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.AllowedSortFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

/**
 * Repository for managing frameworks
 */
@Repository
@Validated
public interface FrameworkRepository extends JpaRepository<Framework, Long> {

    /**
     * Returns true if there is a framework with the given name.
     *
     * @param name The name of the framework to check for.
     * @return A boolean value.
     */
    boolean existsByName(String name);

    /**
     * "Find all frameworks that have a language with the given id, and return them in a pageable format."
     *
     * The @AllowedSortFields annotation is a custom annotation that I created to ensure that the user can only sort by the
     * fields that I want them to
     *
     * @param languageId The id of the language to filter by.
     * @param pageable The pageable object that contains the page number, page size, and sort information.
     * @return A Page of Framework objects.
     */
    Page<Framework> findByLanguage_Id(
            long languageId,
            @AllowedSortFields({"id", "name", "description", "state", "createdAt"}) Pageable pageable
    );

    /**
     * "Find all frameworks with the given state and language id, and return them in the given pageable."
     *
     * The @AllowedSortFields annotation is a custom annotation that I created to ensure that the user can only sort by the
     * fields that I want them to
     *
     * @param state The state of the resource.
     * @param languageId The id of the language to filter by.
     * @param pageable The pageable object that contains the page number, page size, and sort order.
     * @return A Page of Framework objects.
     */
    Page<Framework> findByStateAndLanguage_Id(
            ResourceState state,
            long languageId,
            @AllowedSortFields({"id", "name", "description", "state", "createdAt"}) Pageable pageable
    );
}
