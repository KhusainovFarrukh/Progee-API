package kh.farrukh.progee_api.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kh.farrukh.progee_api.home.HomeController.ENDPOINT_HOME;

/**
 * It's a controller that handles requests to the root of the application
 */
@RestController
@RequestMapping(ENDPOINT_HOME)
public class HomeController {

    public static final String ENDPOINT_HOME = "/";

    public static final String GREETING = """
            Hi! This is Progee-API v1.\s
            Progee-API is REST API for providing information about programming languages, frameworks, their popularity and respect for them among community of developers (with reviews and scores).\s
            Currently working features: languages, frameworks, reviews.\s
            API docs is available at: https://documenter.getpostman.com/view/14256972/UzBgwAPQ

            by Farrukh Khusainov""";

    @GetMapping
    public String home() {
        return GREETING;
    }
}
