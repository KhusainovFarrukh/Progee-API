package kh.farrukh.progee_api;

import kh.farrukh.progee_api.endpoints.auth.AuthService;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.role.RoleService;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class ProgeeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgeeApiApplication.class, args);
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService, RoleService roleService, AuthService authService) {
        return args -> {
            roleService.addRole(new Role(1, "SUPER_ADMIN"));
            roleService.addRole(new Role(2, "ADMIN"));
            roleService.addRole(new Role(3, "USER"));

            userService.addUser(new AppUser(1, "Farrukh", "Khusainov", "farrukh_kh", "1234", new ArrayList<>()));
            userService.addUser(new AppUser(2, "Hamdam", "Unknown", "hamdam_u", "1234", new ArrayList<>()));
            userService.addUser(new AppUser(3, "User", "Userov", "user_u", "1234", new ArrayList<>()));

            authService.addRoleToUser("SUPER_ADMIN", "farrukh_kh");
            authService.addRoleToUser("ADMIN", "farrukh_kh");
            authService.addRoleToUser("USER", "farrukh_kh");
            authService.addRoleToUser("ADMIN", "hamdam_u");
            authService.addRoleToUser("USER", "hamdam_u");
            authService.addRoleToUser("USER", "user_u");
        };
    }

}
