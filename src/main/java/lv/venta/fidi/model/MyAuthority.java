package lv.venta.fidi.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "AuthoritiesTable")
@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = "users")
public class MyAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "authority_id")
    @Setter(AccessLevel.NONE)
    private long authorityId;

    @NotNull
    @Pattern(regexp = "ROLE_[A-Z_]{3,20}")
    @Column(name = "Title", nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "authority", fetch = FetchType.EAGER)
    @JsonIgnore
    private Collection<AppUser> users;

    public MyAuthority(String title) {
        setTitle(title);
    }
}