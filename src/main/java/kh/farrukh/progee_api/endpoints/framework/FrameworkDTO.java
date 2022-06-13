package kh.farrukh.progee_api.endpoints.framework;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * It's a DTO that represents a framework
 */
@Getter
@Setter
@AllArgsConstructor
public class FrameworkDTO {

    @NotNull
    private String name;
    private String description;
    @JsonProperty("image_id")
    private long imageId;
    @NotNull
    @JsonProperty("author_id")
    private long authorId;
//    private long languageId;

}