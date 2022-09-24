package kh.farrukh.progee_api.image.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * It's a DTO that contains the id of an image
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDTO {

    private long id;
}
