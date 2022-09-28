package kh.farrukh.progee_api.global.resource_state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * It's a DTO that contains field `state`, which is the new state of the resource
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SetResourceStateRequestDTO {

    @NotNull
    private ResourceState state;
}
