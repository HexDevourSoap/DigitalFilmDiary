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
import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.WatchEvent;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.service.IWatchEventService;
import lv.venta.fidi.service.OmdbClient;

@Controller
@RequestMapping("/watch-events")
public class WatchEventController {

    @Autowired
    private IWatchEventService watchEventService;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private OmdbClient omdbClient;

    @GetMapping("/create/{imdbId}")
    public String showCreateForm(@PathVariable String imdbId, Model model) {
        try {
            WatchEvent watchEvent = new WatchEvent();
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

            model.addAttribute("watchEvent", watchEvent);
            model.addAttribute("movie", movie);

            return "watch-event-form";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("watchEvent") WatchEvent watchEvent,
                         BindingResult result,
                         @RequestParam("imdbId") String imdbId,
                         Principal principal,
                         HttpServletRequest request,
                         Model model) {
        try {
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

            if (result.hasErrors()) {
                model.addAttribute("movie", movie);
                return "watch-event-form";
            }

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            watchEventService.create(user.getUserId(), imdbId, watchEvent.getWatchedAt(), watchEvent.getNotes());

            return LocaleRedirectPaths.redirectDiary(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            WatchEvent watchEvent = watchEventService.retrieveById(id);
            OmdbMovieDto movie = omdbClient.getByImdbId(watchEvent.getImdbId());

            model.addAttribute("watchEvent", watchEvent);
            model.addAttribute("movie", movie);

            return "watch-event-edit-page";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("watchEvent") WatchEvent watchEvent,
                         BindingResult result,
                         HttpServletRequest request,
                         Model model) {
        try {
            if (result.hasErrors()) {
                OmdbMovieDto movie = omdbClient.getByImdbId(watchEvent.getImdbId());
                model.addAttribute("movie", movie);
                return "watch-event-edit-page";
            }

            watchEventService.update(id, watchEvent.getWatchedAt(), watchEvent.getNotes());
            return LocaleRedirectPaths.redirectDiary(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, HttpServletRequest request, Model model) {
        try {
            watchEventService.deleteById(id);
            return LocaleRedirectPaths.redirectDiary(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}