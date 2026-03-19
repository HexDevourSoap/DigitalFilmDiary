package lv.venta.fidi.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"userMovies", "watchEvents", "ratings", "recommendations", "authority"})
@Table(name = "UsersTable")
@Entity
public class AppUser {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "authority_id", nullable = false)
    private MyAuthority authority;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Collection<UserMovie> userMovies;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Collection<WatchEvent> watchEvents;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Collection<Rating> ratings;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Collection<Recommendation> recommendations;

    public AppUser(String email, String passwordHash, MyAuthority authority) {
        setEmail(email);
        setPasswordHash(passwordHash);
        setAuthority(authority);
    }
}