package lv.venta.fidi.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lv.venta.fidi.config.LocaleRedirectPaths;
import lv.venta.fidi.config.RequestLang;
import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.service.IRatingService;
import lv.venta.fidi.service.MovieTitleUiService;
import lv.venta.fidi.service.OmdbClient;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private IRatingService ratingService;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private OmdbClient omdbClient;

    @Autowired
    private MovieTitleUiService movieTitleUiService;

    @GetMapping("/create/{imdbId}")
    public String showCreateForm(@PathVariable String imdbId, Model model, HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            Rating rating = new Rating();
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

            movieTitleUiService.localizeOmdbTitle(appLang, movie);

            model.addAttribute("rating", rating);
            model.addAttribute("movie", movie);

            return "rating-form";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("rating") Rating rating,
                         BindingResult result,
                         @RequestParam("imdbId") String imdbId,
                         Principal principal,
                         HttpServletRequest request,
                         Model model) {
        try {
            String appLang = RequestLang.appLang(request);
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

            if (result.hasErrors()) {
                movieTitleUiService.localizeOmdbTitle(appLang, movie);
                model.addAttribute("movie", movie);
                return "rating-form";
            }

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            ratingService.create(user.getUserId(), imdbId, rating.getRatingValue());

            return LocaleRedirectPaths.redirectDiary(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            Rating rating = ratingService.retrieveById(id);
            OmdbMovieDto movie = omdbClient.getByImdbId(rating.getImdbId());

            movieTitleUiService.localizeOmdbTitle(appLang, movie);

            model.addAttribute("rating", rating);
            model.addAttribute("movie", movie);

            return "rating-edit-page";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("rating") Rating rating,
                         BindingResult result,
                         HttpServletRequest request,
                         Model model) {
        try {
            String appLang = RequestLang.appLang(request);
            if (result.hasErrors()) {
                OmdbMovieDto movie = omdbClient.getByImdbId(rating.getImdbId());
                movieTitleUiService.localizeOmdbTitle(appLang, movie);
                model.addAttribute("movie", movie);
                return "rating-edit-page";
            }

            ratingService.update(id, rating.getRatingValue());
            return LocaleRedirectPaths.redirectDiary(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}