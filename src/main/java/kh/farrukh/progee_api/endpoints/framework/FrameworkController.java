package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.endpoints.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.global.dto.ResourceStateDTO;
import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.endpoints.framework.FrameworkController.ENDPOINT_FRAMEWORK;

/**
 * Controller that exposes endpoints for managing frameworks
 */
@RestController
@RequestMapping(ENDPOINT_FRAMEWORK)
@RequiredArgsConstructor
public class FrameworkController {

    public static final String ENDPOINT_FRAMEWORK = "/api/v1/frameworks";

    private final FrameworkService frameworkService;

    /**
     * It returns a list (with pagination) of frameworks for a given language
     *
     * @param languageId The id of the language to get frameworks for.
     * @param state      The state of the resource. (Only for admins. Requires access token)
     * @param page       The page number to return. One-based index.
     * @param pageSize   The number of items to return per page. Default is 10.
     * @param sortBy     The field to sort by. Allowed values: id, name, description, state, createdAt. Default is id.
     * @param orderBy    The order in which the results are returned. Allowed values: asc, desc. Default is asc.
     * @return A list of frameworks
     */
    @GetMapping
    public ResponseEntity<PagingResponse<Framework>> getFrameworks(
            @RequestParam(name = "language_id", required = false) Long languageId,
            @RequestParam(name = "state", required = false) ResourceState state,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return new ResponseEntity<>(frameworkService.getFrameworks(
                languageId, state, page, pageSize, sortBy, orderBy
        ), HttpStatus.OK);
    }

    /**
     * This function returns a framework with the given id, if it exists
     *
     * @param id         The id of the framework you want to get
     * @return A ResponseEntity containing Framework object and HttpStatus.
     */
    @GetMapping("{id}")
    public ResponseEntity<Framework> getFrameworkById(@PathVariable long id) {
        return new ResponseEntity<>(frameworkService.getFrameworkById(id), HttpStatus.OK);
    }

    /**
     * This function creates framework if it does not exist.
     *
     * @param frameworkRequestDto Values for the framework to be created.
     * @return A ResponseEntity containing created Framework object and HttpStatus.
     */
    @PostMapping
    public ResponseEntity<Framework> addFramework(
            @Valid @RequestBody FrameworkRequestDTO frameworkRequestDto
    ) {
        return new ResponseEntity<>(frameworkService.addFramework(frameworkRequestDto), HttpStatus.CREATED);
    }

    /**
     * This function updates a framework.
     *
     * @param id           The id of the framework to update
     * @param frameworkRequestDto The framework values that we want to update.
     * @return A ResponseEntity with the updated Framework object and HttpStatus.
     */
    @PutMapping("{id}")
    public ResponseEntity<Framework> updateFramework(
            @PathVariable long id,
            @Valid @RequestBody FrameworkRequestDTO frameworkRequestDto
    ) {
        return new ResponseEntity<>(frameworkService.updateFramework(id, frameworkRequestDto), HttpStatus.OK);
    }

    /**
     * This function deletes a framework from a language
     *
     * @param id         The id of the framework to delete
     * @return A ResponseEntity with HttpStatus.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable long id) {
        frameworkService.deleteFramework(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This function sets the state of the framework with the given id, which belongs to the language with the given id, to the state
     * given in the request body.
     *
     * @param id               The id of the framework to be updated
     * @param resourceStateDto This is the object that contains the state that we want to set the framework to.
     * @return A ResponseEntity with the updated Framework object and HttpStatus.
     */
    @PatchMapping("{id}/state")
    public ResponseEntity<Framework> setFrameworkState(
            @PathVariable long id,
            @Valid @RequestBody ResourceStateDTO resourceStateDto
    ) {
        return new ResponseEntity<>(frameworkService.setFrameworkState(id, resourceStateDto), HttpStatus.OK);
    }
}