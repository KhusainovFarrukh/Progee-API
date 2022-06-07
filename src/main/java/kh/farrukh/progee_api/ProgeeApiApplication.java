package kh.farrukh.progee_api;

import kh.farrukh.progee_api.endpoints.auth.AuthService;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.endpoints.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProgeeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgeeApiApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService, AuthService authService) {
        return args -> {
            userService.addUser(new AppUser("Farrukh", "farrukh@mail.com", "farrukh_kh", "1234", true, false, UserRole.SUPER_ADMIN));
            userService.addUser(new AppUser("Hamdam", "hamdam@mail.com", "hamdam_u", "1234", true, false, UserRole.ADMIN));
            userService.addUser(new AppUser("User", "user@mail.com", "user_u", "1234", true, false, UserRole.USER));
        };
    }

}
