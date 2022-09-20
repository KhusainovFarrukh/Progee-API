package kh.farrukh.progee_api.framework.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import kh.farrukh.progee_api.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.app_user.payloads.AppUserResponseDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "description", "state", "image", "language"})
public class FrameworkResponseDTO {

    private long id;

    private ResourceState state;

    private String name;

    private String description;

    private ImageResponseDTO image;

    private AppUserResponseDTO author;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    private LanguageResponseDTO language;
}
