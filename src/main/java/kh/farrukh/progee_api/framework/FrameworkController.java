package kh.farrukh.progee_api.framework;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/languages/{languageId}/frameworks")
public class FrameworkController {

    private final FrameworkService frameworkService;

    public FrameworkController(FrameworkService frameworkService) {
        this.frameworkService = frameworkService;
    }

    @GetMapping
    public List<Framework> getFrameworksByLanguage(@PathVariable long languageId) {
        return frameworkService.getFrameworksByLanguage(languageId);
    }

    @GetMapping(path = "{id}")
    public Framework getFrameworkById(@PathVariable long languageId, @PathVariable long id) {
        return frameworkService.getFrameworkById(languageId, id);
    }

    @PostMapping
    public void addFramework(@PathVariable long languageId, @RequestBody Framework framework) {
        frameworkService.addFramework(languageId, framework);
    }

    @PutMapping(path = "{id}")
    public void updateFramework(
            @PathVariable long languageId,
            @PathVariable long id,
            @RequestBody Framework framework
    ) {
        frameworkService.updateFramework(languageId, id, framework);
    }

    @DeleteMapping(path = "{id}")
    public void deleteFramework(@PathVariable long languageId, @PathVariable long id) {
        frameworkService.deleteFramework(languageId, id);
    }
}