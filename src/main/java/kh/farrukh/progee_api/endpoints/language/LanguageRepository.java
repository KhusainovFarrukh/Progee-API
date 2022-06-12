package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.utils.sorting.AllowedSortFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

@Validated
public interface LanguageRepository extends JpaRepository<Language, Long> {

    boolean existsByName(String name);

    Page<Language> findByState(
            ResourceState state,
            @AllowedSortFields({"id", "name", "description", "state", "createdAt"}) Pageable pageable
    );
}
