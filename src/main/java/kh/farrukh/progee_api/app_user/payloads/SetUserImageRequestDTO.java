package kh.farrukh.progee_api.app_user.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * It's a DTO that represents a user image change
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetUserImageRequestDTO {
    @NotNull(message = "Image id must not be null")
    @JsonProperty("image_id")
    private long imageId;
}
