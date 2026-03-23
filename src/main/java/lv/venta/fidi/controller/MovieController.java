package lv.venta.fidi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lv.venta.fidi.model.Movie;
import lv.venta.fidi.repo.IMovieRepo;

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private IMovieRepo movieRepo;

    @GetMapping
    public String getAllMovies(Model model) {
        try {
            List<Movie> movies = movieRepo.findAll();
            model.addAttribute("movies", movies);
            return "movies";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/{id}")
    public String getMovieById(@PathVariable Long id, Model model) {
        try {
            Movie movie = movieRepo.findById(id)
                    .orElseThrow(() -> new Exception("Movie not found"));

            model.addAttribute("movie", movie);
            return "movie-details";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}