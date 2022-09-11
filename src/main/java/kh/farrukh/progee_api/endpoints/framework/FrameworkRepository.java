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
}
