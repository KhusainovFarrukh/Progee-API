package kh.farrukh.progee_api.endpoints.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_HOME;

/**
 * It's a controller that handles requests to the root of the application
 */
@RestController
@RequestMapping(ENDPOINT_HOME)
public class HomeController {

    @GetMapping
    public String home() {
        return "Hi! This is Progee-API v1. \nProgee-API is REST API for providing information about programming languages, frameworks, " +
                "their popularity and respect for them among community of developers (with reviews and scores). " +
                "\nCurrently working features: languages, frameworks, reviews. \nAPI docs is available at: https://documenter.getpostman.com/view/14256972/UzBgwAPQ" +
                "\n\nby Farrukh Khusainov";
    }
}
