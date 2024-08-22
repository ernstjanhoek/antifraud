package antifraud.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "app_user")
@Data
public class User {
    public User() {}

    public User(String name, String username, String password, Authority authority, boolean locked) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.authority = authority;
        this.locked = locked;
    }

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Column(unique = true)
    private String username;
    @NotNull
    private String name;
    @NotNull
    private String password;
    private Authority authority;
    private boolean locked;

}