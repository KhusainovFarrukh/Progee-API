package kh.farrukh.progee_api.language;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    boolean existsByName(String name);
}
