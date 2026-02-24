package lv.venta.fidi.model;

import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "MoviesTable")
@Entity
public class Movie   {

	// Fields | Dati
	// =================
	@Setter(value = AccessLevel.NONE)
	@Id
	@Column(name = "movie_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long movieId;

	@NotBlank
	@Size(max = 255)
	@Column(name = "Title", nullable = false)
	private String title;

	@Min(1888)
	@Max(3000)
	@Column(name = "ReleaseYear")
	private Integer releaseYear;

	@Column(name = "RuntimeMin")
	private Integer runtimeMin;

	@Column(name = "Description", columnDefinition = "TEXT")
	private String description;

	// Connections | Saites
	// =================
	@ManyToMany
	@JoinTable(
		name = "MovieGenresTable",
		joinColumns = @JoinColumn(name = "movie_id"),
		inverseJoinColumns = @JoinColumn(name = "genre_id")
	)
	private Collection<Genre> genres;

	@OneToMany(mappedBy = "movie")
	private Collection<UserMovie> userMovies;

	@OneToMany(mappedBy = "movie")
	private Collection<WatchEvent> watchEvents;

	@OneToMany(mappedBy = "movie")
	private Collection<Rating> ratings;

	@OneToMany(mappedBy = "movie")
	private Collection<Recommendation> recommendations;

	// Constructors | Konstruktori
	// =================
	public Movie(String title, Integer releaseYear, Integer runtimeMin, String description) {
		setTitle(title);
		setReleaseYear(releaseYear);
		setRuntimeMin(runtimeMin);
		setDescription(description);
	}
}