package lv.venta.fidi.repo;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Rating;

public interface IRatingRepo extends JpaRepository<Rating, Long> {

    Collection<Rating> findByUser(AppUser user);

    Collection<Rating> findByImdbId(String imdbId);

    Optional<Rating> findByUserAndImdbId(AppUser user, String imdbId);

    boolean existsByUserAndImdbId(AppUser user, String imdbId);
}