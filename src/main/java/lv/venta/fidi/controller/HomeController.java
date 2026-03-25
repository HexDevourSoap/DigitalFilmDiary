package lv.venta.fidi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lv.venta.fidi.service.IMovieService;

@Controller
public class HomeController {

    @Autowired
    private IMovieService movieService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            model.addAttribute("movies", movieService.getAllMovies());
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}