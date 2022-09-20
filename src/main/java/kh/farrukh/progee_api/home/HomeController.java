package kh.farrukh.progee_api.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kh.farrukh.progee_api.home.HomeConstants.ENDPOINT_HOME;
import static kh.farrukh.progee_api.home.HomeConstants.GREETING;

/**
 * It's a controller that handles requests to the root of the application
 */
@RestController
@RequestMapping(ENDPOINT_HOME)
public class HomeController {

    @GetMapping
    public String home() {
        return GREETING;
    }
}
