package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.global.entity.ResourceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

/**
 * Repository for managing frameworks
 */
@Repository
@Validated
public interface FrameworkRepository extends JpaRepository<Framework, Long>, JpaSpecificationExecutor<Framework> {

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
     * @param languageId The id of the language to filter by.
     * @param pageable   The pageable object that contains the page number, page size, and sort information.
     * @return A Page of Framework objects.
     */
    Page<Framework> findByLanguage_Id(
            long languageId,
            Pageable pageable
    );

    /**
     * "Find all frameworks with the given state and language id, and return them in the given pageable."
     * <p>
     *
     * @param state      The state of the resource.
     * @param languageId The id of the language to filter by.
     * @param pageable   The pageable object that contains the page number, page size, and sort order.
     * @return A Page of Framework objects.
     */
    Page<Framework> findByStateAndLanguage_Id(
            ResourceState state,
            long languageId,
            Pageable pageable
    );
}
