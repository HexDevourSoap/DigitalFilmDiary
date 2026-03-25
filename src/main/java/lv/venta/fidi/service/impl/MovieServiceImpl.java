package lv.venta.fidi.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.service.IMovieService;
import lv.venta.fidi.service.OmdbClient;

@Service
public class MovieServiceImpl implements IMovieService {

    @Autowired
    private IMovieRepo movieRepo;

    @Autowired
    private OmdbClient omdbClient;

    @Override
    public List<Movie> getAllMovies() throws Exception {
        return movieRepo.findAll();
    }

    @Override
    public Movie getMovieById(Long id) throws Exception {
        if (id == null || id < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        return movieRepo.findById(id)
                .orElseThrow(() -> new Exception("Movie with ID " + id + " was not found"));
    }

    @Override
    public Movie getOrCreateByImdbId(String imdbId) throws Exception {
        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        imdbId = imdbId.trim();

        Movie existingMovie = movieRepo.findByImdbId(imdbId).orElse(null);
        if (existingMovie != null) {
            return existingMovie;
        }

        OmdbMovieDto dto = omdbClient.getByImdbId(imdbId);

        if (dto == null || dto.getImdbID() == null || dto.getImdbID().isBlank()) {
            throw new Exception("Movie was not found in OMDb");
        }

        Integer year = null;
        Integer runtime = null;
        BigDecimal imdbRating = null;

        try {
            if (dto.getYear() != null && !dto.getYear().isBlank()) {
                String cleanedYear = dto.getYear().replaceAll("[^0-9]", "");
                if (cleanedYear.length() >= 4) {
                    year = Integer.parseInt(cleanedYear.substring(0, 4));
                }
            }
        } catch (Exception e) {
            year = null;
        }

        try {
            if (dto.getRuntime() != null && !dto.getRuntime().isBlank()) {
                String cleanedRuntime = dto.getRuntime().replaceAll("[^0-9]", "");
                if (!cleanedRuntime.isBlank()) {
                    runtime = Integer.parseInt(cleanedRuntime);
                }
            }
        } catch (Exception e) {
            runtime = null;
        }

        try {
            if (dto.getImdbRating() != null
                    && !dto.getImdbRating().isBlank()
                    && !"N/A".equals(dto.getImdbRating())) {
                imdbRating = new BigDecimal(dto.getImdbRating());
            }
        } catch (Exception e) {
            imdbRating = null;
        }

        Movie movie = new Movie(
                dto.getImdbID(),
                dto.getTitle(),
                year,
                runtime,
                dto.getPlot()
        );

        movie.setPosterUrl("N/A".equals(dto.getPoster()) ? null : dto.getPoster());
        movie.setImdbRating(imdbRating);

        return movieRepo.save(movie);
    }
}