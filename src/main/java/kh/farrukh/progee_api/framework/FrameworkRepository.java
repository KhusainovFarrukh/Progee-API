package kh.farrukh.progee_api.framework;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameworkRepository extends JpaRepository<Framework, Long> {

    List<Framework> findByLanguage_Id(long languageId);

    boolean existsByName(String name);
}
