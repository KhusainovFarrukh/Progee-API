package kh.farrukh.progee_api.global.resource_state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceStateDTO {

    @NotNull
    private ResourceState state;
}
