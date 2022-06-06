package kh.farrukh.progee_api.endpoints.language;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LANGUAGE;

@RestController
@RequestMapping(ENDPOINT_LANGUAGE)
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping
    public ResponseEntity<List<Language>> getLanguages() {
        return new ResponseEntity<>(languageService.getLanguages(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Language> getLanguageById(@PathVariable long id) {
        return new ResponseEntity<>(languageService.getLanguageById(id), HttpStatus.OK);
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
    public ResponseEntity<Void> deleteLanguage(@PathVariable long id) {
        languageService.deleteLanguage(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}