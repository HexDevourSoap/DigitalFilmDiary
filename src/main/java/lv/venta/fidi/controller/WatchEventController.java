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
import lv.venta.fidi.model.WatchEvent;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.service.IWatchEventService;

@Controller
@RequestMapping("/watch-events")
public class WatchEventController {

    @Autowired
    private IWatchEventService watchEventService;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private IMovieRepo movieRepo;

    @GetMapping("/create/{movieId}")
    public String showCreateForm(@PathVariable Long movieId, Model model) {
        try {
            WatchEvent watchEvent = new WatchEvent();
            Movie movie = movieRepo.findById(movieId)
                    .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

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
                         @RequestParam("movieId") Long movieId,
                         Principal principal,
                         Model model) {
        try {
            Movie movie = movieRepo.findById(movieId)
                    .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

            if (result.hasErrors()) {
                model.addAttribute("movie", movie);
                return "watch-event-form";
            }

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            watchEventService.create(user.getUserId(), movieId, watchEvent.getWatchedAt(), watchEvent.getNotes());

            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            WatchEvent watchEvent = watchEventService.retrieveById(id);
            model.addAttribute("watchEvent", watchEvent);
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
                         Model model) {
        try {
            if (result.hasErrors()) {
                return "watch-event-edit-page";
            }

            watchEventService.update(id, watchEvent.getWatchedAt(), watchEvent.getNotes());
            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model) {
        try {
            watchEventService.deleteById(id);
            return "redirect:/diary";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}