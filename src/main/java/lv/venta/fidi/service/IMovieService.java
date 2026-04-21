package lv.venta.fidi.service;

import java.util.List;

import lv.venta.fidi.model.Movie;

public interface IMovieService {

    public abstract List<Movie> getAllMovies() throws Exception;

    public abstract List<Movie> getHomeTrendingPreview(int limit) throws Exception;

    public abstract Movie getMovieById(Long id) throws Exception;

    public abstract Movie getOrCreateByImdbId(String imdbId) throws Exception;

    public abstract List<Movie> searchLocalMoviesFuzzy(String query) throws Exception;
}