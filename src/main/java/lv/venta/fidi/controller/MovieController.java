package lv.venta.fidi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import lv.venta.fidi.config.RequestLang;
import lv.venta.fidi.dto.OmdbSearchItemDto;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.service.IMovieService;
import lv.venta.fidi.service.MovieTitleUiService;
import lv.venta.fidi.service.OmdbClient;

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private IMovieService movieService;

    @Autowired
    private OmdbClient omdbClient;

    @Autowired
    private MovieTitleUiService movieTitleUiService;

    @GetMapping
    public String getAllMovies(Model model, HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
            model.addAttribute("lvTitleByMovieId", movieTitleUiService.mapLvTitlesByMovieId(appLang, movies));
            model.addAttribute("lvTitleByImdbId", Collections.emptyMap());
            return "movies";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/{id}")
    public String getMovieById(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            Movie movie = movieService.getMovieById(id);

            movie = movieService.getOrCreateByImdbId(movie.getImdbId());

            model.addAttribute("movie", movie);
            model.addAttribute("movieDisplayTitle",
                    movieTitleUiService.displayMovieTitle(appLang, movie.getImdbId(), movie.getTitle()));
            return "movie-details";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/imdb/{imdbId}")
    public String getMovieByImdbId(@PathVariable String imdbId, Model model, HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            Movie movie = movieService.getOrCreateByImdbId(imdbId);
            model.addAttribute("movie", movie);
            model.addAttribute("movieDisplayTitle",
                    movieTitleUiService.displayMovieTitle(appLang, movie.getImdbId(), movie.getTitle()));
            return "movie-details";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, Model model, HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            if (query == null || query.isBlank()) {
                List<Movie> movies = movieService.getAllMovies();
                model.addAttribute("movies", movies);
                model.addAttribute("lvTitleByMovieId", movieTitleUiService.mapLvTitlesByMovieId(appLang, movies));
                model.addAttribute("lvTitleByImdbId", Collections.emptyMap());
                return "movies";
            }

            List<OmdbSearchItemDto> searchResults = new ArrayList<>();
            Set<String> seenIds = new HashSet<>();

            String[] words = query.trim().split("\\s+");

            for (String word : words) {
                List<OmdbSearchItemDto> partialResults = omdbClient.searchByTitle(word);

                for (OmdbSearchItemDto item : partialResults) {
                    if (item.getImdbID() != null && !seenIds.contains(item.getImdbID())) {
                        searchResults.add(item);
                        seenIds.add(item.getImdbID());
                    }
                }
            }

            model.addAttribute("searchQuery", query);

            if (!searchResults.isEmpty()) {
                model.addAttribute("searchResults", searchResults);
                model.addAttribute("searchFallbackUsed", false);
                model.addAttribute("lvTitleByImdbId", movieTitleUiService.mapLvTitlesByImdbId(appLang, searchResults));
                model.addAttribute("lvTitleByMovieId", Collections.emptyMap());
            } else {
                List<Movie> fallbackMovies = movieService.searchLocalMoviesFuzzy(query);
                model.addAttribute("fallbackMovies", fallbackMovies);
                model.addAttribute("searchFallbackUsed", true);
                model.addAttribute("lvTitleByMovieId", movieTitleUiService.mapLvTitlesByMovieId(appLang, fallbackMovies));
                model.addAttribute("lvTitleByImdbId", Collections.emptyMap());
            }

            return "movies";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}
