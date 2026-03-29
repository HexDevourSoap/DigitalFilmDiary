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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "WatchEventsTable")
@Entity
public class WatchEvent {

    @Setter(AccessLevel.NONE)
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

    @NotBlank
    @Column(name = "imdb_id", nullable = false)
    private String imdbId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    public WatchEvent(AppUser user, String imdbId, LocalDate watchedAt, String notes) {
        setUser(user);
        setImdbId(imdbId);
        setWatchedAt(watchedAt);
        setNotes(notes);
    }

    public WatchEvent(AppUser user, String imdbId, LocalDate watchedAt) {
        setUser(user);
        setImdbId(imdbId);
        setWatchedAt(watchedAt);
    }
}