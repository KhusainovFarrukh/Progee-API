package kh.farrukh.progee_api.endpoints.framework;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * It's a DTO that represents a framework
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FrameworkDTO {

    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, message = "Name must not be shorter than 2 characters")
    private String name;
    private String description;
    @JsonProperty("image_id")
    private long imageId;
    @JsonProperty("language_id")
    private Long languageId;

    public FrameworkDTO(String name, String description, long imageId) {
        this.name = name;
        this.description = description;
        this.imageId = imageId;
    }
}