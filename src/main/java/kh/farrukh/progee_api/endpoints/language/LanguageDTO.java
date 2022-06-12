package kh.farrukh.progee_api.endpoints.language;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class LanguageDTO {

    @NotNull
    private String name;
    private String description;
    @JsonProperty("image_id")
    private long imageId;
    @NotNull
    @JsonProperty("author_id")
    private long authorId;
}