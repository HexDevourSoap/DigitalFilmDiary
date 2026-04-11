package lv.venta.fidi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lv.venta.fidi.model.Genre;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.repo.IGenreRepo;
import lv.venta.fidi.service.IMovieService;

@Controller
public class HomeController {

    @Autowired
    private IMovieService movieService;

    @Autowired
    private IGenreRepo genreRepo;

    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Movie> allMovies = movieService.getAllMovies();
            List<Movie> trendingMovies = allMovies.subList(0, Math.min(12, allMovies.size()));
            List<Genre> genres = genreRepo.findAll();

            Map<Long, Movie> genrePreview = new HashMap<>();

            for (Genre g : genres) {
                for (Movie m : g.getMovies()) {
                    if (m.getPosterUrl() != null && !m.getPosterUrl().isBlank()) {
                        genrePreview.put(g.getGenreId(), m);
                        break;
                    }
                }
            }

            model.addAttribute("movies", trendingMovies);
            model.addAttribute("genres", genres);
            model.addAttribute("genrePreview", genrePreview);

            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}