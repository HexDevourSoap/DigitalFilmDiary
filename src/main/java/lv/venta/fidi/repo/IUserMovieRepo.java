package lv.venta.fidi.repo;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.UserMovie;

public interface IUserMovieRepo extends JpaRepository<UserMovie, Long> {

    Collection<UserMovie> findByUser(AppUser user);

    Collection<UserMovie> findByImdbId(String imdbId);

    Optional<UserMovie> findByUserAndImdbId(AppUser user, String imdbId);

    boolean existsByUserAndImdbId(AppUser user, String imdbId);
}