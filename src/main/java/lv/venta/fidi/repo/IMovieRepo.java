package lv.venta.fidi.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.Movie;

public interface IMovieRepo extends JpaRepository<Movie, Long> {
    boolean existsByImdbId(String imdbId);
}
