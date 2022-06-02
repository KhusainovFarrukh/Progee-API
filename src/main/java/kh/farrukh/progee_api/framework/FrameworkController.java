package kh.farrukh.progee_api.framework;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/languages/{languageId}/frameworks")
public class FrameworkController {

    private final FrameworkService frameworkService;

    public FrameworkController(FrameworkService frameworkService) {
        this.frameworkService = frameworkService;
    }

    @GetMapping
    public ResponseEntity<List<Framework>> getFrameworksByLanguage(@PathVariable long languageId) {
        return new ResponseEntity<>(frameworkService.getFrameworksByLanguage(languageId), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Framework> getFrameworkById(@PathVariable long languageId, @PathVariable long id) {
        return new ResponseEntity<>(frameworkService.getFrameworkById(languageId, id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Framework> addFramework(@PathVariable long languageId, @RequestBody Framework framework) {
        return new ResponseEntity<>(frameworkService.addFramework(languageId, framework), HttpStatus.CREATED);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Framework> updateFramework(
            @PathVariable long languageId,
            @PathVariable long id,
            @RequestBody Framework framework
    ) {
        return new ResponseEntity<>(frameworkService.updateFramework(languageId, id, framework), HttpStatus.OK);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable long languageId, @PathVariable long id) {
        frameworkService.deleteFramework(languageId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}