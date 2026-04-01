package lv.venta.fidi.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.model.WatchEvent;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.service.IRatingService;
import lv.venta.fidi.service.IUserMovieService;
import lv.venta.fidi.service.IWatchEventService;
import lv.venta.fidi.service.OmdbClient;

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
    private OmdbClient omdbClient;

    @GetMapping
    public String retrieveUserDiary(Model model, Principal principal) {
        try {
            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            var diaryEntries = userMovieService.retrieveByUserId(user.getUserId());
            var watchEvents = watchEventService.retrieveByUserId(user.getUserId());
            var ratings = ratingService.retrieveByUserId(user.getUserId());

            Map<String, OmdbMovieDto> movieMap = new HashMap<>();
            Map<String, Rating> ratingMap = new HashMap<>();

            for (UserMovie entry : diaryEntries) {
                movieMap.put(entry.getImdbId(), omdbClient.getByImdbId(entry.getImdbId()));
            }

            for (WatchEvent event : watchEvents) {
                movieMap.put(event.getImdbId(), omdbClient.getByImdbId(event.getImdbId()));
            }

            for (Rating rating : ratings) {
                movieMap.put(rating.getImdbId(), omdbClient.getByImdbId(rating.getImdbId()));
                ratingMap.put(rating.getImdbId(), rating);
            }

            model.addAttribute("diaryEntries", diaryEntries);
            model.addAttribute("watchEvents", watchEvents);
            model.addAttribute("movieMap", movieMap);
            model.addAttribute("ratingMap", ratingMap);

            return "diary-list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/create/{imdbId}")
    public String showCreateForm(@PathVariable String imdbId, Model model) {
        try {
            UserMovie userMovie = new UserMovie();
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

            model.addAttribute("userMovie", userMovie);
            model.addAttribute("movie", movie);
            model.addAttribute("statuses", WatchStatus.values());
            model.addAttribute("ratingValue", null);

            return "diary-form";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("userMovie") UserMovie userMovie,
                         BindingResult result,
                         @RequestParam("imdbId") String imdbId,
                         @RequestParam(name = "ratingValue", required = false) Integer ratingValue,
                         Principal principal,
                         Model model) {
        try {
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

            if (result.hasErrors()) {
                model.addAttribute("movie", movie);
                model.addAttribute("statuses", WatchStatus.values());
                model.addAttribute("ratingValue", ratingValue);
                return "diary-form";
            }

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            userMovieService.create(
                    user.getUserId(),
                    imdbId,
                    userMovie.getStatus(),
                    userMovie.getPlannedDate(),
                    userMovie.getNotes()
            );

            if (ratingValue != null) {
                if (ratingValue < 1 || ratingValue > 10) {
                    throw new Exception("Rating value must be between 1 and 10");
                }
                ratingService.create(user.getUserId(), imdbId, ratingValue);
            }

            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        try {
            UserMovie userMovie = userMovieService.retrieveById(id);
            OmdbMovieDto movie = omdbClient.getByImdbId(userMovie.getImdbId());

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            var existingRating = ratingService.findByUserIdAndImdbId(user.getUserId(), userMovie.getImdbId());

            model.addAttribute("userMovie", userMovie);
            model.addAttribute("movie", movie);
            model.addAttribute("statuses", WatchStatus.values());
            model.addAttribute("ratingValue", existingRating.isPresent() ? existingRating.get().getRatingValue() : null);

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
                         @RequestParam(name = "ratingValue", required = false) Integer ratingValue,
                         Principal principal,
                         Model model) {
        try {
            if (result.hasErrors()) {
                OmdbMovieDto movie = omdbClient.getByImdbId(userMovie.getImdbId());
                model.addAttribute("movie", movie);
                model.addAttribute("statuses", WatchStatus.values());
                model.addAttribute("ratingValue", ratingValue);
                return "diary-edit-page";
            }

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            userMovieService.update(
                    id,
                    userMovie.getStatus(),
                    userMovie.getPlannedDate(),
                    userMovie.getNotes()
            );

            var existingRating = ratingService.findByUserIdAndImdbId(user.getUserId(), userMovie.getImdbId());

            if (ratingValue != null) {
                if (ratingValue < 1 || ratingValue > 10) {
                    throw new Exception("Rating value must be between 1 and 10");
                }

                if (existingRating.isPresent()) {
                    ratingService.update(existingRating.get().getRatingId(), ratingValue);
                } else {
                    ratingService.create(user.getUserId(), userMovie.getImdbId(), ratingValue);
                }
            }

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