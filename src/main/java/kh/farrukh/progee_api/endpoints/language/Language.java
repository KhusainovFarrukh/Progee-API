package kh.farrukh.progee_api.endpoints.language;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_LANGUAGE;

@Entity
@Table(name = TABLE_NAME_LANGUAGE)
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;
    private String description;
    @JsonProperty("has_samples")
    private Boolean hasSamples;

    public Language() {
    }

    public Language(long id, String name, String description, Boolean hasSamples) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hasSamples = hasSamples;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getHasSamples() {
        return hasSamples;
    }

    public void setHasSamples(Boolean hasSamples) {
        this.hasSamples = hasSamples;
    }
}
