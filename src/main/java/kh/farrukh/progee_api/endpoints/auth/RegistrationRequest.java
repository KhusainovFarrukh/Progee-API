package kh.farrukh.progee_api.endpoints.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @NotNull
    private String name;
    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @JsonProperty("image_id")
    private long imageId;
}
