package kh.farrukh.progee_api.app_user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.framework.Framework;
import kh.farrukh.progee_api.global.base_entity.EntityWithId;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.review.Review;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

import static kh.farrukh.progee_api.global.base_entity.EntityWithId.GENERATOR_NAME;
import static kh.farrukh.progee_api.global.utils.constants.DatabaseConstants.SEQUENCE_NAME_USER_ID;
import static kh.farrukh.progee_api.global.utils.constants.DatabaseConstants.TABLE_NAME_USER;

/**
 * AppUser is a simple entity
 * <p>
 * Implements UserDetails to be used in Spring Security logic.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "role", "image", "email", "username"})
@Entity
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_USER_ID)
@Table(name = TABLE_NAME_USER,
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_app_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_app_user_username", columnNames = "username")
        })
public class AppUser extends EntityWithId implements UserDetails {

    private String name;

    private String email;

    @Column(name = "username")
    @JsonProperty("username")
    private String uniqueUsername;

    private String password;

    @JsonProperty("is_enabled")
    // TODO: 6/7/22 set default to false and implement email verification
    private boolean isEnabled = true;

    @JsonProperty("is_locked")
    private boolean isLocked = false;

    @ManyToOne
    @JoinColumn(
            name = "role_id",
            foreignKey = @ForeignKey(name = "fk_role_id_of_app_user")
    )
    private Role role;

    @ManyToOne
    @JoinColumn(
            name = "image_id",
            foreignKey = @ForeignKey(name = "fk_image_id_of_app_user")
    )
    private Image image;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "author")
    private List<Language> createdLanguages;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "author")
    private List<Framework> createdFrameworks;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "author")
    private List<Review> createdReviews;

    @PreRemove
    public void beforeRemove() {
        // if user has created some resources, make their author null ("anonymous" in frontend)
        createdLanguages.forEach(language -> language.setAuthor(null));
        createdFrameworks.forEach(framework -> framework.setAuthor(null));
        createdReviews.forEach(review -> review.setAuthor(null));
    }

    public AppUser(long id) {
        super.setId(id);
    }

    public AppUser(long id, Role role) {
        super.setId(id);
        this.role = role;
    }

    public AppUser(String email, Role role) {
        this.email = email;
        this.role = role;
    }

    public AppUser(String email) {
        this.email = email;
    }

    public AppUser(String name, String uniqueUsername) {
        this.name = name;
        this.uniqueUsername = uniqueUsername;
    }

    public AppUser(String email, long roleId, RoleRepository roleRepository) {
        this.email = email;
        this.role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
    }

    public AppUser(
            String email,
            long roleId,
            RoleRepository roleRepository,
            long imageId,
            ImageRepository imageRepository
    ) {
        this.email = email;
        this.role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        this.image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
    }

    public AppUser(String email, long roleId, String password, RoleRepository roleRepository) {
        this.email = email;
        this.role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        this.password = password;
    }

    public AppUser(String name, String uniqueUsername, long roleId, RoleRepository roleRepository) {
        this.name = name;
        this.uniqueUsername = uniqueUsername;
        this.role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
    }

    public AppUser(String email, String uniqueUsername, String password, Role role) {
        this.email = email;
        this.uniqueUsername = uniqueUsername;
        this.password = password;
        this.role = role;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role
                .getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .toList();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}