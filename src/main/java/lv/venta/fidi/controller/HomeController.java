package lv.venta.fidi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lv.venta.fidi.model.Movie;
import lv.venta.fidi.repo.IMovieRepo;

@Controller
public class HomeController {

    @Autowired
    private IMovieRepo movieRepo;

    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Movie> movies = movieRepo.findAll().stream().limit(12).toList();
            model.addAttribute("movies", movies);
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}