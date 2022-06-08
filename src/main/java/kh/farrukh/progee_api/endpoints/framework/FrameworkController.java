package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_FRAMEWORK;

@RestController
@RequestMapping(ENDPOINT_FRAMEWORK)
@RequiredArgsConstructor
public class FrameworkController {

    private final FrameworkService frameworkService;

    @GetMapping
    public ResponseEntity<PagingResponse<Framework>> getFrameworksByLanguage(
            @PathVariable long languageId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return new ResponseEntity<>(frameworkService.getFrameworksByLanguage(
                languageId, page, pageSize, sortBy, orderBy), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Framework> getFrameworkById(@PathVariable long languageId, @PathVariable long id) {
        return new ResponseEntity<>(frameworkService.getFrameworkById(languageId, id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Framework> addFramework(@PathVariable long languageId, @RequestBody Framework framework) {
        return new ResponseEntity<>(frameworkService.addFramework(languageId, framework), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Framework> updateFramework(
            @PathVariable long languageId,
            @PathVariable long id,
            @RequestBody Framework framework
    ) {
        return new ResponseEntity<>(frameworkService.updateFramework(languageId, id, framework), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable long languageId, @PathVariable long id) {
        frameworkService.deleteFramework(languageId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}