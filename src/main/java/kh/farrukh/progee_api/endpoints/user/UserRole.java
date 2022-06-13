package kh.farrukh.progee_api.endpoints.user;

/**
 * Enum class for roles of users in the application.
 * <p>
 * USER - simple user. Every registered user becomes USER
 * ADMIN - admin. Only super admin make somebody ADMIN. Can approve/decline user-created resources
 * SUPER_ADMIN - admin of all application. Can approve/decline user-created resources. Can change roles of other users
 */
public enum UserRole {
    USER,
    ADMIN,
    SUPER_ADMIN
}