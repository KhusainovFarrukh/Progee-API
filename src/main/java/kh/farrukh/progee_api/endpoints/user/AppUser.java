package kh.farrukh.progee_api.endpoints.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.base.entity.EntityWithId;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_USER;

/**
 * AppUser is a simple entity
 * <p>
 * Implements UserDetails to be used in Spring Security logic.
 */
@Entity
@Table(name = TABLE_NAME_USER)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "role", "image", "email", "username"})
public class AppUser extends EntityWithId implements UserDetails {

    private String name;
    @Column(unique = true)
    private String email;
    @Column(name = "username", unique = true)
    @JsonProperty("username")
    private String uniqueUsername;
    private String password;
    @JsonProperty("is_enabled")
    @Column(name = "is_enabled")
    // TODO: 6/7/22 set default to false and implement email verification
    private boolean isEnabled = true;
    @JsonProperty("is_locked")
    @Column(name = "is_locked")
    private boolean isLocked = false;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @ManyToOne
    private Image image;

    // This is a constructor that takes a AppUserDTO object and
    // sets the values of the current object to the values of
    // the given object.
    public AppUser(AppUserDTO appUserDto, ImageRepository imageRepository) {
        this.name = appUserDto.getName();
        this.email = appUserDto.getEmail();
        this.uniqueUsername = appUserDto.getUsername();
        this.password = appUserDto.getPassword();
        this.role = appUserDto.getRole();
        this.isLocked = appUserDto.isLocked();
        this.isEnabled = appUserDto.isEnabled();
        this.image = imageRepository.findById(appUserDto.getImageId()).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", appUserDto.getImageId())
        );
    }

    public AppUser(long id) {
        super.setId(id);
    }

    public AppUser(String email) {
        this.email = email;
    }

    public AppUser(String name, String uniqueUsername) {
        this.name = name;
        this.uniqueUsername = uniqueUsername;
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
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
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

    @JsonIgnore
    public boolean isAdmin() {
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
    }
}
