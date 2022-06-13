package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_FRAMEWORK;

@RestController
@RequestMapping(ENDPOINT_FRAMEWORK)
@RequiredArgsConstructor
public class FrameworkController {

    private final FrameworkService frameworkService;

    @GetMapping
    public ResponseEntity<PagingResponse<Framework>> getFrameworksByLanguage(
            @PathVariable long languageId,
            @RequestParam(name = "state", required = false) ResourceState state,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return new ResponseEntity<>(frameworkService.getFrameworksByLanguage(
                languageId, state, page, pageSize, sortBy, orderBy), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Framework> getFrameworkById(@PathVariable long languageId, @PathVariable long id) {
        return new ResponseEntity<>(frameworkService.getFrameworkById(languageId, id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Framework> addFramework(
            @PathVariable long languageId,
            @Valid @RequestBody FrameworkDTO frameworkDto
    ) {
        return new ResponseEntity<>(frameworkService.addFramework(languageId, frameworkDto), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Framework> updateFramework(
            @PathVariable long languageId,
            @PathVariable long id,
            @Valid @RequestBody FrameworkDTO frameworkDto
    ) {
        return new ResponseEntity<>(frameworkService.updateFramework(languageId, id, frameworkDto), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable long languageId, @PathVariable long id) {
        frameworkService.deleteFramework(languageId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{id}/state")
    public ResponseEntity<Framework> setFrameworkState(
            @PathVariable long languageId,
            @PathVariable long id,
            @Valid @RequestBody ResourceStateDTO resourceStateDto
    ) {
        return new ResponseEntity<>(frameworkService.setFrameworkState(languageId, id, resourceStateDto), HttpStatus.OK);
    }
}