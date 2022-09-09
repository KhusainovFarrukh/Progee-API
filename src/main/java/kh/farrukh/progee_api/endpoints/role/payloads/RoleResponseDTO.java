package kh.farrukh.progee_api.endpoints.role.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.role.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponseDTO {

    private long id;

    private String title;

    @JsonProperty("is_default")
    private boolean isDefault = false;

    private List<Permission> permissions;
}
