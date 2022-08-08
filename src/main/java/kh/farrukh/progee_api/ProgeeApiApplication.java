package kh.farrukh.progee_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProgeeApiApplication {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //API docs: https://documenter.getpostman.com/view/14256972/UzBgwAPQ
    public static void main(String[] args) {
        SpringApplication.run(ProgeeApiApplication.class, args);
    }
}
