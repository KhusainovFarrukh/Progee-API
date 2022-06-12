package kh.farrukh.progee_api.endpoints.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUniqueUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUniqueUsername(String username);

    boolean existsByEmail(String email);

    // TODO: 6/12/22 add @AllowedSortFields
    @Override
    Page<AppUser> findAll(
//            @AllowedSortFields({"id", "name", "email", "uniqueUsername", "isEnabled", "isLocked", "role"})
            Pageable pageable
    );
}
