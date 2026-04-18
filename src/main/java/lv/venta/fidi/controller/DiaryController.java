package lv.venta.fidi.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lv.venta.fidi.config.LocaleRedirectPaths;
import lv.venta.fidi.config.RequestLang;
import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.service.IRatingService;
import lv.venta.fidi.service.IUserMovieService;
import lv.venta.fidi.service.MovieTitleUiService;
import lv.venta.fidi.service.OmdbClient;

@Controller
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private IUserMovieService userMovieService;

    @Autowired
    private IRatingService ratingService;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private OmdbClient omdbClient;

    @Autowired
    private IMovieRepo movieRepo;

    @Autowired
    private MovieTitleUiService movieTitleUiService;

    @GetMapping
    public String retrieveUserDiary(Model model,
                                    Principal principal,
                                    @RequestParam(name = "from", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                    @RequestParam(name = "to", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                    HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            List<UserMovie> diaryEntries = new ArrayList<>(userMovieService.retrieveByUserId(user.getUserId()));
            List<Rating> ratings = new ArrayList<>(ratingService.retrieveByUserId(user.getUserId()));

            Map<String, OmdbMovieDto> movieMap = new HashMap<>();
            Map<String, Rating> ratingMap = new HashMap<>();

            for (UserMovie entry : diaryEntries) {
                movieMap.put(entry.getImdbId(), resolveMovieForDiary(entry.getImdbId()));
            }

            for (Rating rating : ratings) {
                movieMap.put(rating.getImdbId(), resolveMovieForDiary(rating.getImdbId()));
                ratingMap.put(rating.getImdbId(), rating);
            }

            for (OmdbMovieDto dto : movieMap.values()) {
                movieTitleUiService.localizeOmdbTitle(appLang, dto);
            }

            List<UserMovie> filteredEntries = diaryEntries.stream()
                    .filter(entry -> isInPeriod(entry.getPlannedDate(), fromDate, toDate))
                    .toList();

            List<Rating> filteredRatings = ratings.stream()
                    .filter(r -> r.getRatedAt() != null && isInPeriod(r.getRatedAt().toLocalDate(), fromDate, toDate))
                    .toList();

            Map<String, Integer> watchedPerMonth = buildWatchedPerMonth(filteredEntries);
            BigDecimal averageRating = calculateAverageRating(filteredRatings);
            List<Map.Entry<String, Integer>> topGenres = buildTopGenres(filteredEntries, movieMap);

            model.addAttribute("diaryEntries", filteredEntries);
            model.addAttribute("movieMap", movieMap);
            model.addAttribute("ratingMap", ratingMap);
            model.addAttribute("statsFrom", fromDate);
            model.addAttribute("statsTo", toDate);
            model.addAttribute("watchedPerMonth", watchedPerMonth);
            model.addAttribute("averageRatingPeriod", averageRating);
            model.addAttribute("topGenresPeriod", topGenres);

            return "diary-list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/create/{imdbId}")
    public String showCreateForm(@PathVariable String imdbId, Model model, HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            UserMovie userMovie = new UserMovie();
            OmdbMovieDto movie = omdbClient.getByImdbId(imdbId);

            if (movie == null || movie.getImdbID() == null || movie.getImdbID().isBlank()) {
                throw new Exception("Movie with IMDb ID " + imdbId + " was not found");
            }

            movieTitleUiService.localizeOmdbTitle(appLang, movie);

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

            return LocaleRedirectPaths.redirectDiary(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal,
                               HttpServletRequest request) {
        try {
            String appLang = RequestLang.appLang(request);
            UserMovie userMovie = userMovieService.retrieveById(id);
            OmdbMovieDto movie = omdbClient.getByImdbId(userMovie.getImdbId());

            AppUser user = appUserRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new Exception("User was not found"));

            var existingRating = ratingService.findByUserIdAndImdbId(user.getUserId(), userMovie.getImdbId());

            movieTitleUiService.localizeOmdbTitle(appLang, movie);

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
                         HttpServletRequest request,
                         Model model) {
        try {
            String appLang = RequestLang.appLang(request);
            if (result.hasErrors()) {
                OmdbMovieDto movie = omdbClient.getByImdbId(userMovie.getImdbId());
                movieTitleUiService.localizeOmdbTitle(appLang, movie);
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

            return LocaleRedirectPaths.redirectDiary(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, HttpServletRequest request, Model model) {
        try {
            userMovieService.deleteById(id);
            return LocaleRedirectPaths.redirectDiary(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "show-error-page";
        }
    }

    /**
     * Diary list uses OMDb for metadata; when the API fails or returns no poster, use the local {@link Movie} row.
     */
    private OmdbMovieDto resolveMovieForDiary(String imdbId) {
        OmdbMovieDto dto = omdbClient.getByImdbId(imdbId);
        Optional<Movie> localOpt = movieRepo.findByImdbId(imdbId);
        if (localOpt.isEmpty()) {
            return dto;
        }
        Movie local = localOpt.get();
        if (dto == null) {
            dto = new OmdbMovieDto();
            dto.setImdbID(local.getImdbId());
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            dto.setTitle(local.getTitle());
        }
        if (dto.getPoster() == null || dto.getPoster().isBlank() || "N/A".equalsIgnoreCase(dto.getPoster())) {
            if (local.getPosterUrl() != null && !local.getPosterUrl().isBlank()) {
                dto.setPoster(local.getPosterUrl());
            }
        }
        return dto;
    }

    private boolean isInPeriod(LocalDate date, LocalDate fromDate, LocalDate toDate) {
        if (date == null) {
            return fromDate == null && toDate == null;
        }
        if (fromDate != null && date.isBefore(fromDate)) {
            return false;
        }
        if (toDate != null && date.isAfter(toDate)) {
            return false;
        }
        return true;
    }

    private Map<String, Integer> buildWatchedPerMonth(List<UserMovie> entries) {
        Map<YearMonth, Integer> grouped = new HashMap<>();
        for (UserMovie entry : entries) {
            if (entry.getStatus() != WatchStatus.WATCHED || entry.getPlannedDate() == null) {
                continue;
            }
            YearMonth ym = YearMonth.from(entry.getPlannedDate());
            grouped.put(ym, grouped.getOrDefault(ym, 0) + 1);
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Integer> result = new LinkedHashMap<>();
        grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> result.put(e.getKey().format(fmt), e.getValue()));
        return result;
    }

    private BigDecimal calculateAverageRating(List<Rating> ratings) {
        if (ratings.isEmpty()) {
            return null;
        }
        int sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getRatingValue();
        }
        return BigDecimal.valueOf(sum)
                .divide(BigDecimal.valueOf(ratings.size()), 1, RoundingMode.HALF_UP);
    }

    private List<Map.Entry<String, Integer>> buildTopGenres(List<UserMovie> entries, Map<String, OmdbMovieDto> movieMap) {
        Map<String, Integer> genreCounts = new HashMap<>();
        for (UserMovie entry : entries) {
            OmdbMovieDto movie = movieMap.get(entry.getImdbId());
            if (movie == null || movie.getGenre() == null || movie.getGenre().isBlank()) {
                continue;
            }
            String[] genres = movie.getGenre().split(",");
            for (String genre : genres) {
                String trimmed = genre.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                genreCounts.put(trimmed, genreCounts.getOrDefault(trimmed, 0) + 1);
            }
        }
        return genreCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .toList();
    }
}