package kh.farrukh.progee_api.endpoints.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUniqueUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUniqueUsername(String username);

    boolean existsByEmail(String email);
}
