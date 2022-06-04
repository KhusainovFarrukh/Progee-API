package kh.farrukh.progee_api;

import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserService;
import kh.farrukh.progee_api.endpoints.role.Role;
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
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.addRole(new Role(1, "SUPER_ADMIN"));
            userService.addRole(new Role(2, "ADMIN"));
            userService.addRole(new Role(3, "USER"));

            userService.addUser(new AppUser(1, "Farrukh", "Khusainov", "farrukh@mail.com", "farrukh_kh", "1234", new ArrayList<>()));
            userService.addUser(new AppUser(2, "Hamdam", "Unknown", "hamdam@mail.com", "hamdam_u", "1234", new ArrayList<>()));
            userService.addUser(new AppUser(3, "User", "Userov", "user@mail.com", "user_u", "1234", new ArrayList<>()));

            userService.addRoleToUser("SUPER_ADMIN", "farrukh_kh");
            userService.addRoleToUser("ADMIN", "farrukh_kh");
            userService.addRoleToUser("USER", "farrukh_kh");
            userService.addRoleToUser("ADMIN", "hamdam_u");
            userService.addRoleToUser("USER", "hamdam_u");
            userService.addRoleToUser("USER", "user_u");
        };
    }

}
