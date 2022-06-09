package kh.farrukh.progee_api;

import kh.farrukh.progee_api.endpoints.auth.AuthService;
import kh.farrukh.progee_api.endpoints.framework.FrameworkDTO;
import kh.farrukh.progee_api.endpoints.framework.FrameworkService;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.LanguageDTO;
import kh.farrukh.progee_api.endpoints.language.LanguageService;
import kh.farrukh.progee_api.endpoints.review.ReviewDTO;
import kh.farrukh.progee_api.endpoints.review.ReviewService;
import kh.farrukh.progee_api.endpoints.review.ReviewValue;
import kh.farrukh.progee_api.endpoints.user.AppUserDTO;
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
    CommandLineRunner run(
            LanguageService languageService,
            FrameworkService frameworkService,
            ReviewService reviewService,
            UserService userService,
            AuthService authService,
            ImageRepository imageRepository
    ) {
        return args -> {
            imageRepository.save(new Image(1L, "fake.jpeg"));

            userService.addUser(new AppUserDTO("Farrukh", "farrukh@mail.com", "farrukh_kh", "1234", true, false, UserRole.SUPER_ADMIN, 1L));
            userService.addUser(new AppUserDTO("Hamdam", "hamdam@mail.com", "hamdam_u", "1234", true, false, UserRole.ADMIN, 1L));
            userService.addUser(new AppUserDTO("User", "user@mail.com", "user_u", "1234", true, false, UserRole.USER, 1L));

            languageService.addLanguage(new LanguageDTO("Java", "Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.", 1, 1));
            languageService.addLanguage(new LanguageDTO("Kotlin", "A modern programming language that makes developers happier. · Multiplatform Mobile · Server-side · Web Frontend · Android.", 1, 1));
            languageService.addLanguage(new LanguageDTO("Python", "Python is a high-level, interpreted, general-purpose programming language.", 1, 1));
            languageService.addLanguage(new LanguageDTO("JavaScript", "JavaScript often abbreviated JS, is a programming language that is one of the core technologies of the World Wide Web, alongside HTML and CSS.", 1, 1));

            frameworkService.addFramework(1, new FrameworkDTO("Spring Boot", "Spring Boot — The Spring Framework is an application framework and inversion of control container for the Java platform.", 1, 1));
            frameworkService.addFramework(1, new FrameworkDTO("JRockit", "Java profiling tool for performance Tuning.", 1, 2));
            frameworkService.addFramework(1, new FrameworkDTO("JSoup", "Java HTML parser library. Supports extracting and manipulating data using DOM, CSS, and JQuery methods.", 1, 1));
            frameworkService.addFramework(3, new FrameworkDTO("Django", "Django is the most popular high-level web application development framework that encourages us to build Python applications very quickly.", 1, 1));
            frameworkService.addFramework(3, new FrameworkDTO("CubicWeb", "CubicWeb is an open-source, semantic, and free Python web framework.", 1, 3));
            frameworkService.addFramework(2, new FrameworkDTO("Ktor", "Ktor is a framework for quickly creating web applications in Kotlin with minimal effort.", 1, 2));
            frameworkService.addFramework(2, new FrameworkDTO("Kweb", "Kweb is a new way to create beautiful, efficient, and scalable websites in Kotlin, quickly", 1, 2));

            for (int i = 1; i < 50; i++) {
                reviewService.addReview(1, new ReviewDTO("This is " + i + " review for language (like)", ReviewValue.LIKE, i % 3, i % 5, 1));
                reviewService.addReview(1, new ReviewDTO("This is " + i + " review for language (don't have pratice)", ReviewValue.DONT_HAVE_PRACTICE, i % 10, i % 25, 2));

                if (i % 2 == 0) {
                    reviewService.addReview(4, new ReviewDTO("This is " + i + " review for language (dislike)", ReviewValue.DISLIKE, 3));
                } else {
                    reviewService.addReview(2, new ReviewDTO("This is " + i + " review for language (like)", ReviewValue.LIKE, 1));
                }
            }
        };
    }
}
