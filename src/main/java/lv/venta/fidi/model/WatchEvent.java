package lv.venta.fidi.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "WatchEventsTable")
@Entity
public class WatchEvent   {

	// Fields | Dati
	// =================
	@Setter(value = AccessLevel.NONE)
	@Id
	@Column(name = "watch_event_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long watchEventId;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "WatchedAt", nullable = false)
	private LocalDate watchedAt;

	@Column(name = "Notes", columnDefinition = "TEXT")
	private String notes;

	// Connections | Saites
	// =================
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser user;

	@ManyToOne
	@JoinColumn(name = "movie_id", nullable = false)
	private Movie movie;

	// Constructors | Konstruktori
	// =================
	public WatchEvent(AppUser user, Movie movie, LocalDate watchedAt, String notes) {
		setUser(user);
		setMovie(movie);
		setWatchedAt(watchedAt);
		setNotes(notes);
	}

	public WatchEvent(AppUser user, Movie movie, LocalDate watchedAt) {
		setUser(user);
		setMovie(movie);
		setWatchedAt(watchedAt);
	}
}