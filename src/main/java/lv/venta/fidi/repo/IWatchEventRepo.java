package lv.venta.fidi.repo;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.WatchEvent;

public interface IWatchEventRepo extends JpaRepository<WatchEvent, Long> {

    public abstract Collection<WatchEvent> findByUser(AppUser user);

    public abstract Collection<WatchEvent> findByMovie(Movie movie);

    public abstract Collection<WatchEvent> findByUserAndMovie(AppUser user, Movie movie);

    Collection<WatchEvent> findByWatchedAt(LocalDate watchedAt);

}