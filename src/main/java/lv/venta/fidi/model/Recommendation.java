package lv.venta.fidi.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
	name = "RecommendationsTable",
	uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "movie_id" })
)
@Entity
public class Recommendation  {

	// Fields | Dati
	// =================
	@Setter(value = AccessLevel.NONE)
	@Id
	@Column(name = "recommendation_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long recommendationId;

	@Column(name = "Score", nullable = false)
	private BigDecimal score;

	@Column(name = "Reason")
	private String reason;

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
	public Recommendation(AppUser user, Movie movie, BigDecimal score, String reason) {
		setUser(user);
		setMovie(movie);
		setScore(score);
		setReason(reason);
	}
}