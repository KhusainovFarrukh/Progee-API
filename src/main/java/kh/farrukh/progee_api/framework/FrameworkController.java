package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.framework.payloads.FrameworkResponseDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.resource_state.SetResourceStateRequestDTO;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.framework.FrameworkConstants.ENDPOINT_FRAMEWORK;

/**
 * Controller that exposes endpoints for managing frameworks
 */
@RestController
@RequestMapping(ENDPOINT_FRAMEWORK)
@RequiredArgsConstructor
public class FrameworkController {

    private final FrameworkService frameworkService;

    /**
     * It returns a list (with pagination) of frameworks
     *
     * @param languageId The id of the language to get frameworks for (Optional).
     * @param state      The state of the resource. (Optional. Only for admins. Requires access token)
     * @param page       The page number to return. One-based index.
     * @param pageSize   The number of items to return per page. Default is 10.
     * @param sortBy     The field to sort by. Allowed values: id, name, description, state, createdAt. Default is id.
     * @param orderBy    The order in which the results are returned. Allowed values: asc, desc. Default is asc.
     * @return A list of frameworks
     */
    @GetMapping
    public ResponseEntity<PagingResponse<FrameworkResponseDTO>> getFrameworks(
            @RequestParam(name = "language_id", required = false) Long languageId,
            @RequestParam(name = "state", required = false) ResourceState state,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return ResponseEntity.ok(frameworkService.getFrameworks(
                languageId, state, page, pageSize, sortBy, orderBy
        ));
    }

    /**
     * This function returns a framework with the given id, if it exists
     *
     * @param id The id of the framework you want to get
     * @return A ResponseEntity containing FrameworkResponseDTO object and HttpStatus.
     */
    @GetMapping("{id}")
    public ResponseEntity<FrameworkResponseDTO> getFrameworkById(@PathVariable long id) {
        return ResponseEntity.ok(frameworkService.getFrameworkById(id));
    }

    /**
     * This function creates framework if it does not exist.
     *
     * @param frameworkRequestDto Values for the framework to be created.
     * @return A ResponseEntity containing created FrameworkResponseDTO object and HttpStatus.
     */
    @PostMapping
    public ResponseEntity<FrameworkResponseDTO> addFramework(
            @Valid @RequestBody FrameworkRequestDTO frameworkRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(frameworkService.addFramework(frameworkRequestDto));
    }

    /**
     * This function updates a framework.
     *
     * @param id                  The id of the framework to update
     * @param frameworkRequestDto The framework values that we want to update.
     * @return A ResponseEntity with the updated FrameworkResponseDTO object and HttpStatus.
     */
    @PutMapping("{id}")
    public ResponseEntity<FrameworkResponseDTO> updateFramework(
            @PathVariable long id,
            @Valid @RequestBody FrameworkRequestDTO frameworkRequestDto
    ) {
        return ResponseEntity.ok(frameworkService.updateFramework(id, frameworkRequestDto));
    }

    /**
     * This function deletes a framework from a language
     *
     * @param id The id of the framework to delete
     * @return A ResponseEntity with HttpStatus.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable long id) {
        frameworkService.deleteFramework(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * This function sets the state of the framework with the given id, to the state
     * given in the request body.
     *
     * @param id               The id of the framework to be updated
     * @param setResourceStateRequestDto This is the object that contains the state that we want to set the framework to.
     * @return A ResponseEntity with the updated FrameworkResponseDTO object and HttpStatus.
     */
    @PatchMapping("{id}/state")
    public ResponseEntity<FrameworkResponseDTO> setFrameworkState(
            @PathVariable long id,
            @Valid @RequestBody SetResourceStateRequestDTO setResourceStateRequestDto
    ) {
        return ResponseEntity.ok(frameworkService.setFrameworkState(id, setResourceStateRequestDto));
    }
}