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
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(
    name = "RatingsTable",
    uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "imdb_id" })
)
@Entity
public class Rating {

    @Setter(AccessLevel.NONE)
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

    @NotBlank
    @Column(name = "imdb_id", nullable = false)
    private String imdbId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    public Rating(AppUser user, String imdbId, int ratingValue) {
        setUser(user);
        setImdbId(imdbId);
        setRatingValue(ratingValue);
        setRatedAt(LocalDateTime.now());
    }
}