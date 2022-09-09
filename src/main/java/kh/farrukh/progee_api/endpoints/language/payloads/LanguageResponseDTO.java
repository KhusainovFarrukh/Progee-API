package kh.farrukh.progee_api.endpoints.language.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.image.payloads.ImageResponseDTO;
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
public class LanguageResponseDTO {

    private long id;

    private ResourceState state;

    private String name;

    private String description;

    private ImageResponseDTO image;

    private AppUserResponseDTO author;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

}
