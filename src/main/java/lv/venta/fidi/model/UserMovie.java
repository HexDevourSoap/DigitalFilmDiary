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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lv.venta.fidi.enums.WatchStatus;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(
    name = "UserMoviesTable",
    uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "imdb_id" })
)
@Entity
public class UserMovie {

    @Setter(AccessLevel.NONE)
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

    @NotBlank
    @Column(name = "imdb_id", nullable = false)
    private String imdbId;

    @Column(name = "Notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private AppUser user;

    public UserMovie(AppUser user, String imdbId, WatchStatus status, LocalDate plannedDate) {
        setUser(user);
        setImdbId(imdbId);
        setStatus(status);
        setPlannedDate(plannedDate);
    }

    public UserMovie(AppUser user, String imdbId, WatchStatus status, LocalDate plannedDate, String notes) {
        setUser(user);
        setImdbId(imdbId);
        setStatus(status);
        setPlannedDate(plannedDate);
        setNotes(notes);
    }

    public UserMovie(AppUser user, String imdbId, WatchStatus status) {
        setUser(user);
        setImdbId(imdbId);
        setStatus(status);
    }
}