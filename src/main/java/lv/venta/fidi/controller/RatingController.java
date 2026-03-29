package lv.venta.fidi.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;
import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.service.IRatingService;
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

    @GetMapping("/create/{imdbId}")
    public String showCreateForm(@PathVariable String imdbId, Model model) {
        try {
            Rating rating = new Rating();
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

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
                         Model model) {
        try {
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

            if (result.hasErrors()) {
                model.addAttribute("movie", movie);
                return "rating-form";
            }

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            ratingService.create(user.getUserId(), imdbId, rating.getRatingValue());

            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Rating rating = ratingService.retrieveById(id);
            OmdbMovieDto movie = omdbClient.getByImdbId(rating.getImdbId());

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
                         Model model) {
        try {
            if (result.hasErrors()) {
                OmdbMovieDto movie = omdbClient.getByImdbId(rating.getImdbId());
                model.addAttribute("movie", movie);
                return "rating-edit-page";
            }

            ratingService.update(id, rating.getRatingValue());
            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}