package kh.farrukh.progee_api.endpoints.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

    private String title;

    @Enumerated(EnumType.STRING)
    private List<Permission> permissions;
}
