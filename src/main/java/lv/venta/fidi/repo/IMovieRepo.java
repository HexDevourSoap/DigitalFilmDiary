package lv.venta.fidi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.Movie;

public interface IMovieRepo extends JpaRepository<Movie, Long> {

    public abstract boolean existsByImdbId(String imdbId);

    public abstract Optional<Movie> findByImdbId(String imdbId);
}