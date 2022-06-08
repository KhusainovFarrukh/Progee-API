package kh.farrukh.progee_api.endpoints.framework;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrameworkRepository extends JpaRepository<Framework, Long> {

    List<Framework> findByLanguage_Id(long languageId);

    Page<Framework> findByLanguage_Id(long languageId, Pageable pageable);

    boolean existsByName(String name);
}
