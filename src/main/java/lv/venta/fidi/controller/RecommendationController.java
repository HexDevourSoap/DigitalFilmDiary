package lv.venta.fidi.controller;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

import lv.venta.fidi.config.RequestLang;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.Recommendation;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.service.IRecommendationService;
import lv.venta.fidi.service.MovieTitleUiService;

@Controller
public class RecommendationController {

    @Autowired
    private IRecommendationService recommendationService;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private MovieTitleUiService movieTitleUiService;

    @GetMapping("/recommendations")
    public String showRecommendations(Model model,
                                      Principal principal,
                                      HttpServletRequest request,
                                      @RequestParam(name = "refresh", defaultValue = "false") boolean refresh) {
        try {
            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            String appLang = RequestLang.appLang(request);
            var recs = recommendationService.retrieveByUserId(user.getUserId());
            if (refresh || recs == null || recs.isEmpty()) {
                recommendationService.generateRecommendationsForUser(user.getUserId(), appLang);
                recs = recommendationService.retrieveByUserId(user.getUserId());
            }
            model.addAttribute("recommendations", recs);
            List<Movie> recMovies = recs.stream().map(Recommendation::getMovie).toList();
            model.addAttribute("lvTitleByMovieId", movieTitleUiService.mapLvTitlesByMovieId(appLang, recMovies));
            model.addAttribute("lvTitleByImdbId", Collections.emptyMap());

            return "recommendations";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}