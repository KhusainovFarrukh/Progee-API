package kh.farrukh.progee_api.language;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/languages")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public ResponseEntity<List<Language>> getLanguages() {
        return new ResponseEntity<>(languageService.getLanguages(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public Language getLanguageById(@PathVariable long id) {
        return languageService.getLanguageById(id);
    }

    @PostMapping
    public ResponseEntity<Language> addLanguage(@RequestBody Language language) {
        return new ResponseEntity<>(languageService.addLanguage(language), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Language> updateLanguage(@PathVariable long id, @RequestBody Language language) {
        return new ResponseEntity<>(languageService.updateLanguage(id, language), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLanguage(@PathVariable long id) {
        languageService.deleteLanguage(id);
        return new ResponseEntity<>("Language deleted successfully", HttpStatus.NO_CONTENT);
    }
}