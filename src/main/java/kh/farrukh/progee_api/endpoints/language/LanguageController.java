package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LANGUAGE;

@RestController
@RequestMapping(ENDPOINT_LANGUAGE)
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping
    public ResponseEntity<PagingResponse<Language>> getLanguages(
            @RequestParam(name = "state", required = false) ResourceState state,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return new ResponseEntity<>(
                languageService.getLanguages(state, page, pageSize, sortBy, orderBy), HttpStatus.OK
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<Language> getLanguageById(@PathVariable long id) {
        return new ResponseEntity<>(languageService.getLanguageById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Language> addLanguage(@RequestBody LanguageDTO languageDto) {
        return new ResponseEntity<>(languageService.addLanguage(languageDto), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Language> updateLanguage(@PathVariable long id, @RequestBody LanguageDTO languageDto) {
        return new ResponseEntity<>(languageService.updateLanguage(id, languageDto), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable long id) {
        languageService.deleteLanguage(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{id}/state")
    public ResponseEntity<Language> setLanguageState(
            @PathVariable long id,
            @RequestBody ResourceStateDTO resourceStateDto
    ) {
        return new ResponseEntity<>(languageService.setLanguageState(id, resourceStateDto), HttpStatus.OK);
    }
}