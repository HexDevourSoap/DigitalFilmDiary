package lv.venta.fidi.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import lv.venta.fidi.config.RequestLang;
import lv.venta.fidi.model.Genre;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.repo.IGenreRepo;
import lv.venta.fidi.service.GenreLabelUiService;
import lv.venta.fidi.service.IMovieService;
import lv.venta.fidi.service.MovieTitleUiService;

@Controller
public class HomeController {

    @Autowired
    private IMovieService movieService;

    @Autowired
    private IGenreRepo genreRepo;

    @Autowired
    private MovieTitleUiService movieTitleUiService;

    @Autowired
    private GenreLabelUiService genreLabelUiService;

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            List<Movie> trendingMovies = movieService.getHomeTrendingPreview(12);
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
            model.addAttribute("lvTitleByMovieId", movieTitleUiService.mapLvTitlesByMovieId(appLang, trendingMovies));
            model.addAttribute("lvTitleByImdbId", Collections.emptyMap());
            model.addAttribute("genres", genres);
            model.addAttribute("genrePreview", genrePreview);
            model.addAttribute("genreDisplayNames", genreLabelUiService.mapDisplayNamesByGenreId(appLang, genres));

            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}