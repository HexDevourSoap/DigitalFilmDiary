package lv.venta.fidi.repo;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.WatchEvent;

public interface IWatchEventRepo extends JpaRepository<WatchEvent, Long> {

    public abstract Collection<WatchEvent> findByUser(AppUser user);

    public abstract Collection<WatchEvent> findByImdbId(String imdbId);

    public abstract Collection<WatchEvent> findByUserAndImdbId(AppUser user, String imdbId);

    public abstract Collection<WatchEvent> findByWatchedAt(LocalDate watchedAt);
}