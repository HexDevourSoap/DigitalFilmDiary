package lv.venta.fidi.service;

import java.util.List;

import lv.venta.fidi.model.Movie;

public interface IMovieService {

    List<Movie> getAllMovies() throws Exception;

    Movie getMovieById(Long id) throws Exception;

    Movie getOrCreateByImdbId(String imdbId) throws Exception;
}