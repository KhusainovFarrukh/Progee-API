package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.language.Language;

import javax.persistence.*;

@Entity
@Table(name = "frameworks")
public class Framework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;
    private String description;

    @ManyToOne
    private Language language;

    public Framework() {
    }

    public Framework(long id, String name, String description, Boolean hasSamples, long languageId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.language = new Language(languageId, "", "", false);
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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId, "", "", false));
    }
}
