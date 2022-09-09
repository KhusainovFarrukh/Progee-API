package kh.farrukh.progee_api.endpoints.role.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.role.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequestDTO {

    private String title;

    @JsonProperty("is_default")
    private boolean isDefault;

    @Enumerated(EnumType.STRING)
    private List<Permission> permissions;
}
