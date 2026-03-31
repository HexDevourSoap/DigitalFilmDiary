package lv.venta.fidi.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.service.IRecommendationService;

@Controller
public class RecommendationController {

    @Autowired
    private IRecommendationService recommendationService;

    @Autowired
    private IAppUserRepo appUserRepo;

    @GetMapping("/recommendations")
    public String showRecommendations(Model model, Principal principal) {
        try {
            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            recommendationService.generateRecommendationsForUser(user.getUserId());
            model.addAttribute("recommendations", recommendationService.retrieveByUserId(user.getUserId()));

            return "recommendations";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }
}