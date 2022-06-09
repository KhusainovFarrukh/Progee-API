package kh.farrukh.progee_api.endpoints.framework;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FrameworkDTO {

    private String name;
    private String description;
    @JsonProperty("image_id")
    private long imageId;
    @JsonProperty("author_id")
    private long authorId;
//    private long languageId;

}