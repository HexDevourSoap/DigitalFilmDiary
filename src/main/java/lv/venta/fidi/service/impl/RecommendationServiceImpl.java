package lv.venta.fidi.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Genre;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.model.Recommendation;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.repo.IRatingRepo;
import lv.venta.fidi.repo.IRecommendationRepo;
import lv.venta.fidi.repo.IUserMovieRepo;
import lv.venta.fidi.service.IRecommendationService;

@Service
@Transactional
public class RecommendationServiceImpl implements IRecommendationService {
    private static final int MAX_RECOMMENDATIONS = 16;
    private static final int MAX_REASON_GENRES = 3;
    private static final int TRENDING_FILL_LIMIT = 4;

    @Autowired
    private IRecommendationRepo recommendationRepo;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private IMovieRepo movieRepo;

    @Autowired
    private IRatingRepo ratingRepo;

    @Autowired
    private IUserMovieRepo userMovieRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public void generateRecommendationsForUser(Long userId, String appLang) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        boolean english = "en".equalsIgnoreCase(appLang);

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        Collection<Rating> userRatings = ratingRepo.findByUser(user);
        Collection<UserMovie> userDiary = userMovieRepo.findByUser(user);
        Set<String> alreadyKnownImdbIds = new HashSet<>();

        for (Rating rating : userRatings) {
            alreadyKnownImdbIds.add(rating.getImdbId());
        }

        for (UserMovie entry : userDiary) {
            alreadyKnownImdbIds.add(entry.getImdbId());
        }

        Map<String, Integer> genreScores = new HashMap<>();

        for (Rating rating : userRatings) {
            if (rating.getRatingValue() >= 8) {
                Movie movie = movieRepo.findByImdbId(rating.getImdbId()).orElse(null);
                if (movie != null && movie.getGenres() != null) {
                    int weight = switch (rating.getRatingValue()) {
                        case 10 -> 3;
                        case 9 -> 2;
                        default -> 1;
                    };

                    for (Genre genre : movie.getGenres()) {
                        genreScores.put(genre.getName(), genreScores.getOrDefault(genre.getName(), 0) + weight);
                    }
                }
            }
        }

        for (UserMovie entry : userDiary) {
            int diaryWeight = switch (entry.getStatus()) {
                case WATCHED -> 2;
                case WATCHING -> 1;
                case PLAN_TO_WATCH -> 1;
            };

            Movie movie = movieRepo.findByImdbId(entry.getImdbId()).orElse(null);
            if (movie != null && movie.getGenres() != null) {
                for (Genre genre : movie.getGenres()) {
                    genreScores.put(genre.getName(), genreScores.getOrDefault(genre.getName(), 0) + diaryWeight);
                }
            }
        }

        Collection<Movie> allMovies = movieRepo.findAll();
        for (Movie m : allMovies) {
            if (m.getGenres() != null) {
                m.getGenres().size();
            }
        }

        recommendationRepo.deleteByUser(user);
        entityManager.flush();
        entityManager.clear();

        AppUser userReloaded = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        List<CandidateRecommendation> candidates = new ArrayList<>();
        for (Movie candidate : allMovies) {

            if (alreadyKnownImdbIds.contains(candidate.getImdbId())) {
                continue;
            }

            if (candidate.getGenres() == null || candidate.getGenres().isEmpty()) {
                continue;
            }

            int score = 0;
            List<String> matchedGenres = new ArrayList<>();

            for (Genre genre : candidate.getGenres()) {
                Integer genreScore = genreScores.get(genre.getName());
                if (genreScore != null && genreScore > 0) {
                    score += genreScore;
                    matchedGenres.add(genre.getName());
                }
            }

            
            if (score <= 0) {
                continue;
            }

            int reasonGenreCount = Math.min(MAX_REASON_GENRES, matchedGenres.size());
            List<String> genreNamesForReason = matchedGenres.subList(0, reasonGenreCount).stream()
                    .map(g -> english ? g : toLatvianGenre(g))
                    .toList();
            String reason = english
                    ? "Recommended because you like " + String.join(", ", genreNamesForReason)
                    : "Ieteikts, jo tev patīk " + String.join(", ", genreNamesForReason);
            BigDecimal recommendationScore = BigDecimal.valueOf(score);

            candidates.add(new CandidateRecommendation(
                    candidate,
                    recommendationScore,
                    reason
            ));
        }

        candidates.sort(
                Comparator
                        .comparing(CandidateRecommendation::score).reversed()
                        .thenComparing(c -> c.movie().getReleaseYear(), Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(c -> c.movie().getImdbRating(), Comparator.nullsLast(Comparator.reverseOrder()))
        );

        int primaryLimit = Math.max(0, MAX_RECOMMENDATIONS - TRENDING_FILL_LIMIT);
        int limit = Math.min(primaryLimit, candidates.size());
        Set<String> selectedImdbIds = new HashSet<>();
        for (int i = 0; i < limit; i++) {
            CandidateRecommendation candidate = candidates.get(i);
            selectedImdbIds.add(candidate.movie().getImdbId());
            var existingOpt = recommendationRepo.findByUserAndMovie(userReloaded, candidate.movie());
            if (existingOpt.isPresent()) {
                Recommendation existing = existingOpt.get();
                existing.setScore(candidate.score());
                existing.setReason(candidate.reason());
                recommendationRepo.save(existing);
            } else {
                recommendationRepo.save(new Recommendation(userReloaded, candidate.movie(), candidate.score(), candidate.reason()));
            }
        }

        int remainingSlots = MAX_RECOMMENDATIONS - limit;
        int trendingLimit = Math.min(TRENDING_FILL_LIMIT, remainingSlots);
        List<Movie> trendingCandidates = allMovies.stream()
                .filter(m -> !alreadyKnownImdbIds.contains(m.getImdbId()))
                .filter(m -> !selectedImdbIds.contains(m.getImdbId()))
                .sorted(
                        Comparator
                                .comparing((Movie m) -> trendScore(m)).reversed()
                                .thenComparing(Movie::getReleaseYear, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(Movie::getImdbRating, Comparator.nullsLast(Comparator.reverseOrder()))
                )
                .limit(trendingLimit)
                .toList();

        for (Movie trendingMovie : trendingCandidates) {
            String reason = english
                    ? "Recommended because this is a popular or newer film"
                    : "Ieteikts, jo šī ir populāra vai jaunāka filma";
            BigDecimal score = BigDecimal.valueOf(trendScore(trendingMovie));
            recommendationRepo.save(new Recommendation(userReloaded, trendingMovie, score, reason));
        }
    }

    @Override
    public Collection<Recommendation> retrieveByUserId(Long userId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        return recommendationRepo.findByUser(user).stream()
                .sorted(Comparator.comparing(Recommendation::getScore).reversed())
                .toList();
    }

    @Override
    public void clearRecommendationsForUser(Long userId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        recommendationRepo.deleteByUser(user);
    }

    private record CandidateRecommendation(Movie movie, BigDecimal score, String reason) { }

    private double trendScore(Movie movie) {
        double imdb = movie.getImdbRating() != null ? movie.getImdbRating().doubleValue() : 0.0;
        int year = movie.getReleaseYear() != null ? movie.getReleaseYear() : 1900;

        
        double recencyBonus;
        if (year >= 2023) {
            recencyBonus = 3.0;
        } else if (year >= 2018) {
            recencyBonus = 2.0;
        } else if (year >= 2010) {
            recencyBonus = 1.0;
        } else {
            recencyBonus = 0.0;
        }

        return imdb + recencyBonus;
    }

    private String toLatvianGenre(String genreName) {
        if (genreName == null) {
            return "";
        }
        return switch (genreName.toLowerCase()) {
            case "action" -> "asa sižeta";
            case "adventure" -> "piedzīvojumu";
            case "animation" -> "animācijas";
            case "biography" -> "biogrāfijas";
            case "comedy" -> "komēdijas";
            case "crime" -> "kriminālfilmas";
            case "documentary" -> "dokumentālās";
            case "drama" -> "drāmas";
            case "family" -> "ģimenes";
            case "fantasy" -> "fantāzijas";
            case "history" -> "vēsturiskās";
            case "horror" -> "šausmu";
            case "music" -> "muzikālās";
            case "musical" -> "mūzikla";
            case "mystery" -> "mistērijas";
            case "romance" -> "romantikas";
            case "sci-fi", "science fiction" -> "zinātniskās fantastikas";
            case "sport" -> "sporta";
            case "thriller" -> "trillera";
            case "war" -> "kara";
            case "western" -> "vesternu";
            default -> genreName;
        };
    }
}