package kh.farrukh.progee_api.endpoints.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_USER;

@Entity
@Table(name = TABLE_NAME_USER)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
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

    public AppUser(String name,
                   String email,
                   String uniqueUsername,
                   String password,
                   UserRole role) {
        this.name = name;
        this.email = email;
        this.uniqueUsername = uniqueUsername;
        this.password = password;
        this.role = role;
    }

    public AppUser(String name,
                   String email,
                   String uniqueUsername,
                   String password,
                   boolean isEnabled,
                   boolean isLocked,
                   UserRole role) {
        this.name = name;
        this.email = email;
        this.uniqueUsername = uniqueUsername;
        this.password = password;
        this.isEnabled = isEnabled;
        this.isLocked = isLocked;
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
}
