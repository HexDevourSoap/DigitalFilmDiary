package lv.venta.fidi.model;

import java.math.BigDecimal;
import java.util.Collection;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {
        "genres",
        "userMovies",
        "watchEvents",
        "ratings",
        "recommendations"
})
@Entity
@Table(
    name = "MoviesTable",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "imdb_id")
    }
)
public class Movie {

    // =========================
    // Primary key
    // =========================
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private long movieId;

    // =========================
    // OMDb / IMDb fields
    // =========================
    @NotBlank
    @Size(max = 16)
    @Column(name = "imdb_id", nullable = false, unique = true)
    private String imdbId;

    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl;

    @Column(name = "imdb_rating", precision = 3, scale = 1)
    private BigDecimal imdbRating;

    // =========================
    // Movie information
    // =========================
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

    // =========================
    // Relationships
    // =========================
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

    // =========================
    // Convenience constructor
    // =========================
    public Movie(
            String imdbId,
            String title,
            Integer releaseYear,
            Integer runtimeMin,
            String description
    ) {
        this.imdbId = imdbId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.runtimeMin = runtimeMin;
        this.description = description;
    }
}