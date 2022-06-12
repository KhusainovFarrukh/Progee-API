package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.utils.sorting.AllowedSortFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

@Validated
public interface FrameworkRepository extends JpaRepository<Framework, Long> {

    Page<Framework> findByLanguage_Id(
            long languageId,
            @AllowedSortFields({"id", "name", "description", "state", "createdAt"}) Pageable pageable
    );

    Page<Framework> findByStateAndLanguage_Id(
            ResourceState state,
            long languageId,
            @AllowedSortFields({"id", "name", "description", "state", "createdAt"}) Pageable pageable
    );

    boolean existsByName(String name);
}
