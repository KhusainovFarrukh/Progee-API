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

    // Comment out to create a new CommandLineRunner bean that will run the code
    // inside the run() method when the application starts and populate some mock data
/*    @Bean
    CommandLineRunner run(
            LanguageService languageService,
            FrameworkService frameworkService,
            ReviewService reviewService,
            UserService userService,
            ImageRepository imageRepository
    ) {
        return args -> {
            try {
                imageRepository.save(new Image(1L, null));

                userService.addUser(new AppUserDTO("Farrukh", "farrukh@mail.com", "farrukh_kh", "1234", true, false, UserRole.SUPER_ADMIN, 1L));
                userService.addUser(new AppUserDTO("Hamdam", "hamdam@mail.com", "hamdam_u", "1234", true, false, UserRole.ADMIN, 1L));
                userService.addUser(new AppUserDTO("User", "user@mail.com", "user_u", "1234", true, false, UserRole.USER, 1L));

                GrantedAuthority authority = new SimpleGrantedAuthority(UserRole.SUPER_ADMIN.name());
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        "farrukh@mail.com", null, Collections.singletonList(authority)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                languageService.addLanguage(new LanguageDTO("Java", "Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.", 1));
                languageService.addLanguage(new LanguageDTO("Kotlin", "A modern programming language that makes developers happier. · Multiplatform Mobile · Server-side · Web Frontend · Android.", 1));
                languageService.addLanguage(new LanguageDTO("Python", "Python is a high-level, interpreted, general-purpose programming language.", 1));
                languageService.addLanguage(new LanguageDTO("JavaScript", "JavaScript often abbreviated JS, is a programming language that is one of the core technologies of the World Wide Web, alongside HTML and CSS.", 1));

                frameworkService.addFramework(1, new FrameworkDTO("Spring Boot", "Spring Boot — The Spring Framework is an application framework and inversion of control container for the Java platform.", 1));
                frameworkService.addFramework(1, new FrameworkDTO("JRockit", "Java profiling tool for performance Tuning.", 1));
                frameworkService.addFramework(1, new FrameworkDTO("JSoup", "Java HTML parser library. Supports extracting and manipulating data using DOM, CSS, and JQuery methods.", 1));
                frameworkService.addFramework(3, new FrameworkDTO("Django", "Django is the most popular high-level web application development framework that encourages us to build Python applications very quickly.", 1));
                frameworkService.addFramework(3, new FrameworkDTO("CubicWeb", "CubicWeb is an open-source, semantic, and free Python web framework.", 1));
                frameworkService.addFramework(2, new FrameworkDTO("Ktor", "Ktor is a framework for quickly creating web applications in Kotlin with minimal effort.", 1));
                frameworkService.addFramework(2, new FrameworkDTO("Kweb", "Kweb is a new way to create beautiful, efficient, and scalable websites in Kotlin, quickly", 1));

                for (int i = 1; i < 50; i++) {
                    reviewService.addReview(1, new ReviewDTO("This is " + i + " review for language (like)", ReviewValue.LIKE));
                    reviewService.addReview(1, new ReviewDTO("This is " + i + " review for language (don't have pratice)", ReviewValue.DONT_HAVE_PRACTICE));

                    if (i % 2 == 0) {
                        reviewService.addReview(4, new ReviewDTO("This is " + i + " review for language (dislike)", ReviewValue.DISLIKE));
                    } else {
                        reviewService.addReview(2, new ReviewDTO("This is " + i + " review for language (like)", ReviewValue.LIKE));
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        };
    }*/
}
