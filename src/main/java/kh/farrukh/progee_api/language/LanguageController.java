package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceStateDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller that exposes endpoints for managing frameworks
 */
@RestController
@RequestMapping(LanguageController.ENDPOINT_LANGUAGE)
@RequiredArgsConstructor
public class LanguageController {

    public static final String ENDPOINT_LANGUAGE = "/api/v1/languages";

    private final LanguageService languageService;

    /**
     * It returns a list (with pagination) of languages
     *
     * @param state    The state of the resource. (Only for admins. Requires access token)
     * @param page     The page number to return. One-based index.
     * @param pageSize The number of items to return per page. Default is 10.
     * @param sortBy   The field to sort by. Allowed values: id, name, description, state, createdAt. Default is id.
     * @param orderBy  The order in which the results are returned. Allowed values: asc, desc. Default is asc.
     * @return A list of languages
     */
    @GetMapping
    public ResponseEntity<PagingResponse<LanguageResponseDTO>> getLanguages(
            @RequestParam(name = "state", required = false) ResourceState state,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return ResponseEntity.ok(languageService.getLanguages(state, page, pageSize, sortBy, orderBy));
    }

    /**
     * This function returns a language with the given id, if it exists
     *
     * @param id The id of the language you want to get
     * @return A ResponseEntity containing Language object and HttpStatus.
     */
    @GetMapping("{id}")
    public ResponseEntity<LanguageResponseDTO> getLanguageById(@PathVariable long id) {
        return ResponseEntity.ok(languageService.getLanguageById(id));
    }

    /**
     * This function creates language if it does not exist.
     *
     * @param languageRequestDto Values for the language to be created.
     * @return A ResponseEntity containing created Language object and HttpStatus.
     */
    @PostMapping
    public ResponseEntity<LanguageResponseDTO> addLanguage(@Valid @RequestBody LanguageRequestDTO languageRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(languageService.addLanguage(languageRequestDto));
    }

    /**
     * This function updates a language.
     *
     * @param id                 The id of the language to update
     * @param languageRequestDto The language values that we want to update.
     * @return A ResponseEntity with the updated Language object and HttpStatus.
     */
    @PutMapping("{id}")
    public ResponseEntity<LanguageResponseDTO> updateLanguage(
            @PathVariable long id,
            @Valid @RequestBody LanguageRequestDTO languageRequestDto
    ) {
        return ResponseEntity.ok(languageService.updateLanguage(id, languageRequestDto));
    }

    /**
     * This function deletes a language
     *
     * @param id The id of the language to delete
     * @return A ResponseEntity with HttpStatus.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable long id) {
        languageService.deleteLanguage(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * This function sets the state of the language with the given id, to the state
     * given in the request body.
     *
     * @param id               The id of the language to be updated
     * @param resourceStateDto This is the object that contains the state that we want to set the language to.
     * @return A ResponseEntity with the updated Language object and HttpStatus.
     */
    @PatchMapping("{id}/state")
    public ResponseEntity<LanguageResponseDTO> setLanguageState(
            @PathVariable long id,
            @Valid @RequestBody ResourceStateDTO resourceStateDto
    ) {
        return ResponseEntity.ok(languageService.setLanguageState(id, resourceStateDto));
    }
}