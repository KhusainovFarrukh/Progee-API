package kh.farrukh.progee_api.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class HomeController {

    @GetMapping
    public String home() {
        return "Hi! This is Progee-API v1. \nProgee-API is REST API for providing information about programming languages, frameworks, " +
                "their popularity and respect for them among community of developers (with reviews and scores). " +
                "\nCurrently working features: languages, frameworks, reviews. \n\nby Farrukh Khusainov";
    }
}
