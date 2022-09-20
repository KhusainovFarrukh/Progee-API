package kh.farrukh.progee_api.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByTitle(String title);

    Optional<Role> findFirstByIsDefaultIsTrue();

    Optional<Role> findFirstByIsDefaultIsTrueAndIdNot(Long id);

    long countByIsDefaultIsTrue();
}
