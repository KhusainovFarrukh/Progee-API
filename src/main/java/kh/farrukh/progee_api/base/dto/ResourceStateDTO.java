package kh.farrukh.progee_api.base.dto;

import kh.farrukh.progee_api.base.entity.ResourceState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceStateDTO {

    private ResourceState state;
}
