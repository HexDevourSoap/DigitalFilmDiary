package lv.venta.fidi.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.service.IRatingService;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private IRatingService ratingService;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private IMovieRepo movieRepo;

    @GetMapping("/create/{movieId}")
    public String showCreateForm(@PathVariable Long movieId, Model model) {
        try {
            Rating rating = new Rating();
            Movie movie = movieRepo.findById(movieId)
                    .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

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
                         @RequestParam("movieId") Long movieId,
                         Principal principal,
                         Model model) {
        try {
            Movie movie = movieRepo.findById(movieId)
                    .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

            if (result.hasErrors()) {
                model.addAttribute("movie", movie);
                return "rating-form";
            }

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            ratingService.create(user.getUserId(), movieId, rating.getRatingValue());

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
            model.addAttribute("rating", rating);
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