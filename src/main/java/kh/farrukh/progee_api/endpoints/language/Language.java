package kh.farrukh.progee_api.endpoints.language;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_LANGUAGE;

@Entity
@Table(name = TABLE_NAME_LANGUAGE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;
    private String description;
    @JsonProperty("has_samples")
    private Boolean hasSamples;
}
