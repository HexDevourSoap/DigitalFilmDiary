package lv.venta.fidi.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lv.venta.fidi.dto.OmdbSearchItemDto;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.service.IMovieService;
import lv.venta.fidi.service.OmdbClient;

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private IMovieService movieService;

    @Autowired
    private OmdbClient omdbClient;

    @GetMapping
    public String getAllMovies(Model model) {
        try {
            model.addAttribute("movies", movieService.getAllMovies());
            return "movies";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/{id}")
    public String getMovieById(@PathVariable Long id, Model model) {
        try {
            Movie movie = movieService.getMovieById(id);

            movie = movieService.getOrCreateByImdbId(movie.getImdbId());

            model.addAttribute("movie", movie);
            return "movie-details";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/imdb/{imdbId}")
    public String getMovieByImdbId(@PathVariable String imdbId, Model model) {
        try {
            model.addAttribute("movie", movieService.getOrCreateByImdbId(imdbId));
            return "movie-details";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, Model model) {
        try {
            if (query == null || query.isBlank()) {
                model.addAttribute("movies", movieService.getAllMovies());
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
            } else {
                List<Movie> fallbackMovies = movieService.searchLocalMoviesFuzzy(query);
                model.addAttribute("fallbackMovies", fallbackMovies);
                model.addAttribute("searchFallbackUsed", true);
            }

            return "movies";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}