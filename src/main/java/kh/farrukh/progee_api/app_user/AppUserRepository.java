package kh.farrukh.progee_api.app_user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Repository for managing frameworks
 */
@Repository
@Validated
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Find an AppUser by email.
     *
     * @param email The email address of the user you want to find.
     * @return Optional<AppUser>
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * Check if a user exists by their unique username.
     *
     * @param username The username to check for.
     * @return A boolean value.
     */
    boolean existsByUniqueUsername(String username);

    /**
     * Check if there is a user with the given email.
     *
     * @param email The email to check.
     * @return A boolean value.
     */
    boolean existsByEmail(String email);
}
