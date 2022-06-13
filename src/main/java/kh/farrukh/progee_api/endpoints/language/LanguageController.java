package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LANGUAGE;

/**
 * Controller that exposes endpoints for managing frameworks
 */
@RestController
@RequestMapping(ENDPOINT_LANGUAGE)
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    /**
     * It returns a list (with pagination) of languages for a given language
     *
     * @param state    The state of the resource. (Only for admins. Requires access token)
     * @param page     The page number to return. One-based index.
     * @param pageSize The number of items to return per page. Default is 10.
     * @param sortBy   The field to sort by. Allowed values: id, name, description, state, createdAt. Default is id.
     * @param orderBy  The order in which the results are returned. Allowed values: asc, desc. Default is asc.
     * @return A list of languages
     */
    @GetMapping
    public ResponseEntity<PagingResponse<Language>> getLanguages(
            @RequestParam(name = "state", required = false) ResourceState state,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return new ResponseEntity<>(
                languageService.getLanguages(state, page, pageSize, sortBy, orderBy), HttpStatus.OK
        );
    }

    /**
     * This function returns a language with the given id, if it exists
     *
     * @param id The id of the language you want to get
     * @return A ResponseEntity containing Language object and HttpStatus.
     */
    @GetMapping("{id}")
    public ResponseEntity<Language> getLanguageById(@PathVariable long id) {
        return new ResponseEntity<>(languageService.getLanguageById(id), HttpStatus.OK);
    }

    /**
     * This function creates language if it does not exist.
     *
     * @param languageDto Values for the language to be created.
     * @return A ResponseEntity containing created Language object and HttpStatus.
     */
    @PostMapping
    public ResponseEntity<Language> addLanguage(@Valid @RequestBody LanguageDTO languageDto) {
        return new ResponseEntity<>(languageService.addLanguage(languageDto), HttpStatus.CREATED);
    }

    /**
     * This function updates a language.
     *
     * @param id           The id of the language to update
     * @param languageDto The language values that we want to update.
     * @return A ResponseEntity with the updated Language object and HttpStatus.
     */
    @PutMapping("{id}")
    public ResponseEntity<Language> updateLanguage(
            @PathVariable long id,
            @Valid @RequestBody LanguageDTO languageDto
    ) {
        return new ResponseEntity<>(languageService.updateLanguage(id, languageDto), HttpStatus.OK);
    }

    /**
     * This function deletes a language
     *
     * @param id         The id of the language to delete
     * @return A ResponseEntity with HttpStatus.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable long id) {
        languageService.deleteLanguage(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This function sets the state of the language with the given id, to the state
     * given in the request body.
     *
     * @param id               The id of the language to be updated
     * @param resourceStateDto This is the object that contains the state that we want to set the language to.
     * @return A ResponseEntity with the updated Language object and HttpStatus.
     */
    @PutMapping("{id}/state")
    public ResponseEntity<Language> setLanguageState(
            @PathVariable long id,
            @Valid @RequestBody ResourceStateDTO resourceStateDto
    ) {
        return new ResponseEntity<>(languageService.setLanguageState(id, resourceStateDto), HttpStatus.OK);
    }
}