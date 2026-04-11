package lv.venta.fidi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lv.venta.fidi.model.Genre;
import lv.venta.fidi.repo.IGenreRepo;

@Controller
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private IGenreRepo genreRepo;

    @GetMapping("/{id}")
    public String getGenre(@PathVariable Long id, Model model) {
        Genre genre = genreRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        model.addAttribute("genre", genre);
        model.addAttribute("movies", genre.getMovies());

        return "genre-movies";
    }
}