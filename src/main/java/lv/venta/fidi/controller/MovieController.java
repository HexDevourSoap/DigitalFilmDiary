package lv.venta.fidi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lv.venta.fidi.service.IMovieService;

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private IMovieService movieService;

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
            model.addAttribute("movie", movieService.getMovieById(id));
            return "movie-details";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}