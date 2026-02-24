package lv.venta.fidi.model;

import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
//import lv.venta.model.base.BaseAuditEntity;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "UsersTable")
@Entity
public class AppUser   {

	// Fields | Dati
	// =================
	@Setter(value = AccessLevel.NONE)
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

	// Connections | Saites
	// =================
	@OneToMany(mappedBy = "user")
	private Collection<UserMovie> userMovies;

	@OneToMany(mappedBy = "user")
	private Collection<WatchEvent> watchEvents;

	@OneToMany(mappedBy = "user")
	private Collection<Rating> ratings;

	@OneToMany(mappedBy = "user")
	private Collection<Recommendation> recommendations;

	// Constructors | Konstruktori
	// =================
	public AppUser(String email, String passwordHash) {
		setEmail(email);
		setPasswordHash(passwordHash);
	}
}