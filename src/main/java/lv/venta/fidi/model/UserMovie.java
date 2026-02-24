package lv.venta.fidi.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lv.venta.fidi.enums.WatchStatus;
//import lv.venta.fidi.model.base.BaseAuditEntity;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(
	name = "UserMoviesTable", // MYSQL - User_Movies_Table
	uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "movie_id" })
)
@Entity
public class UserMovie {

	// Fields | Dati
	// =================
	@Setter(value = AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_movie_id")
	private long userMovieId;

	@NotNull
	@Column(name = "Status", nullable = false)
	@Enumerated(EnumType.STRING)
	private WatchStatus status;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "PlannedDate")
	private LocalDate plannedDate;

	// Connections | Saites
	// =================
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@ToString.Exclude
	private AppUser user;

	@ManyToOne
	@JoinColumn(name = "movie_id", nullable = false)
	@ToString.Exclude
	private Movie movie;

	// Constructors | Konstruktori
	// =================
	public UserMovie(AppUser user, Movie movie, WatchStatus status, LocalDate plannedDate) {
		setUser(user);
		setMovie(movie);
		setStatus(status);
		setPlannedDate(plannedDate);
	}

	public UserMovie(AppUser user, Movie movie, WatchStatus status) {
		setUser(user);
		setMovie(movie);
		setStatus(status);
	}
}