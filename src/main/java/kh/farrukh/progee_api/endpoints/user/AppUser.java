package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.util.Collection;
import java.util.stream.Collectors;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_USER;

@Entity
@Table(name = TABLE_NAME_USER)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles;

    public User toUser() {
        return new User(this.username, this.password, this.roles.stream().map(
                (role) -> new SimpleGrantedAuthority(role.getTitle())
        ).collect(Collectors.toList()));
    }
}
