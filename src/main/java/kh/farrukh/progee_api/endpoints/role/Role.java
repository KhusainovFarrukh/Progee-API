package kh.farrukh.progee_api.endpoints.role;

import javax.persistence.*;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_ROLE;

@Entity
@Table(name = TABLE_NAME_ROLE)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;

    public Role() {
    }

    public Role(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
}
