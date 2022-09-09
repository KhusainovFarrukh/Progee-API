package kh.farrukh.progee_api.endpoints.framework.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.user.payloads.AppUserResponseDTO;
import kh.farrukh.progee_api.global.entity.ResourceState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FrameworkResponseDTO {

    private long id;

    private ResourceState state;

    private String name;

    private String description;

    private Image image;

    private AppUserResponseDTO author;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    private Language language;
}
