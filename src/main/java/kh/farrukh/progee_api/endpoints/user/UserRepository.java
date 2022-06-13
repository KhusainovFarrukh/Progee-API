package kh.farrukh.progee_api.endpoints.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Repository for managing frameworks
 */
@Validated
public interface UserRepository extends JpaRepository<AppUser, Long> {

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

    /**
     * This function returns a page of AppUser objects, and the page
     *
     * @param pageable The pageable object that contains the page number, page size, and sort information.
     * @return A Page of AppUser objects.
     */
    @Override
    Page<AppUser> findAll(
            // TODO: 6/12/22 add @AllowedSortFields
//            @AllowedSortFields({"id", "name", "email", "uniqueUsername", "isEnabled", "isLocked", "role"})
            Pageable pageable
    );
}
