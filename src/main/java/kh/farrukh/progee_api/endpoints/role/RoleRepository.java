package kh.farrukh.progee_api.endpoints.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByTitle(String title);

    boolean existsByTitle(String title);
}
