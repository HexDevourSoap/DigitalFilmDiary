package lv.venta.fidi.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;
import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.service.IRatingService;
import lv.venta.fidi.service.IUserMovieService;
import lv.venta.fidi.service.IWatchEventService;

@Controller
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private IUserMovieService userMovieService;

    @Autowired
    private IWatchEventService watchEventService;

    @Autowired
    private IRatingService ratingService;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private IMovieRepo movieRepo;

    @GetMapping
    public String retrieveUserDiary(Model model, Principal principal) {
        try {
            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            model.addAttribute("diaryEntries", userMovieService.retrieveByUserId(user.getUserId()));
            model.addAttribute("watchEvents", watchEventService.retrieveByUserId(user.getUserId()));
            model.addAttribute("ratings", ratingService.retrieveByUserId(user.getUserId()));

            return "diary-list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/create/{movieId}")
    public String showCreateForm(@PathVariable Long movieId, Model model) {
        try {
            UserMovie userMovie = new UserMovie();
            Movie movie = movieRepo.findById(movieId)
                    .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

            model.addAttribute("userMovie", userMovie);
            model.addAttribute("movie", movie);
            model.addAttribute("statuses", WatchStatus.values());

            return "diary-form";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("userMovie") UserMovie userMovie,
                         BindingResult result,
                         @RequestParam("movieId") Long movieId,
                         Principal principal,
                         Model model) {
        try {
            Movie movie = movieRepo.findById(movieId)
                    .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

            if (result.hasErrors()) {
                model.addAttribute("movie", movie);
                model.addAttribute("statuses", WatchStatus.values());
                return "diary-form";
            }

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            userMovieService.create(user.getUserId(), movieId, userMovie.getStatus(), userMovie.getPlannedDate());

            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            UserMovie userMovie = userMovieService.retrieveById(id);
            model.addAttribute("userMovie", userMovie);
            model.addAttribute("statuses", WatchStatus.values());

            return "diary-edit-page";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("userMovie") UserMovie userMovie,
                         BindingResult result,
                         Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("statuses", WatchStatus.values());
                return "diary-edit-page";
            }

            userMovieService.update(id, userMovie.getStatus(), userMovie.getPlannedDate());
            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model) {
        try {
            userMovieService.deleteById(id);
            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}