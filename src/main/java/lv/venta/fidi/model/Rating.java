package lv.venta.fidi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(
	name = "RatingsTable",
	uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "movie_id" })
)
@Entity
public class Rating {

	// Fields | Dati
	// =================
	@Setter(value = AccessLevel.NONE)
	@Id
	@Column(name = "rating_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long ratingId;

	@Min(1)
	@Max(10)
	@Column(name = "RatingValue", nullable = false)
	private int ratingValue;

	@Column(name = "RatedAt")
	private LocalDateTime ratedAt;

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
	public Rating(AppUser user, Movie movie, int ratingValue) {
		setUser(user);
		setMovie(movie);
		setRatingValue(ratingValue);
		setRatedAt(LocalDateTime.now());
	}
}