package lv.venta.fidi.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import lv.venta.fidi.config.RequestLang;
import lv.venta.fidi.model.Genre;
import lv.venta.fidi.repo.IGenreRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.service.MovieTitleUiService;

@Controller
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private IGenreRepo genreRepo;

    @Autowired
    private IMovieRepo movieRepo;

    @Autowired
    private MovieTitleUiService movieTitleUiService;

    @GetMapping("/{id}")
    public String getGenre(@PathVariable Long id,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "12") int size,
                           Model model,
                           HttpServletRequest request) {
        String appLang = RequestLang.appLang(request);
        Genre genre = genreRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        int safeSize = Math.min(Math.max(size, 6), 24);
        var pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by("title").ascending());
        var moviesPage = movieRepo.findByGenresGenreId(id, pageable);

        model.addAttribute("genre", genre);
        var movies = moviesPage.getContent();
        model.addAttribute("movies", movies);
        model.addAttribute("lvTitleByMovieId", movieTitleUiService.mapLvTitlesByMovieId(appLang, movies));
        model.addAttribute("lvTitleByImdbId", Collections.emptyMap());
        model.addAttribute("page", moviesPage.getNumber());
        model.addAttribute("size", safeSize);
        model.addAttribute("totalPages", moviesPage.getTotalPages());
        model.addAttribute("totalMovies", moviesPage.getTotalElements());
        model.addAttribute("hasPrev", moviesPage.hasPrevious());
        model.addAttribute("hasNext", moviesPage.hasNext());

        return "genre-movies";
    }
}