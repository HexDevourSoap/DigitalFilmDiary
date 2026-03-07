package lv.venta.fidi.repo;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.Rating;

public interface IRatingRepo extends JpaRepository<Rating, Long> {

    public abstract Collection<Rating> findByUser(AppUser user);

    public abstract Collection<Rating> findByMovie(Movie movie);

    public abstract Optional<Rating> findByUserAndMovie(AppUser user, Movie movie);

    public abstract boolean existsByUserAndMovie(AppUser user, Movie movie);

}