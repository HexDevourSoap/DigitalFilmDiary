package lv.venta.fidi.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Genre;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.model.Recommendation;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.model.WatchEvent;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.repo.IRatingRepo;
import lv.venta.fidi.repo.IRecommendationRepo;
import lv.venta.fidi.repo.IUserMovieRepo;
import lv.venta.fidi.repo.IWatchEventRepo;
import lv.venta.fidi.service.IRecommendationService;

@Service
@Transactional
public class RecommendationServiceImpl implements IRecommendationService {

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
    private IWatchEventRepo watchEventRepo;

    @Override
    public void generateRecommendationsForUser(Long userId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        recommendationRepo.deleteByUser(user);

        Collection<Rating> userRatings = ratingRepo.findByUser(user);
        Collection<UserMovie> userDiary = userMovieRepo.findByUser(user);
        Collection<WatchEvent> userWatchEvents = watchEventRepo.findByUser(user);

        Set<String> alreadyKnownImdbIds = new HashSet<>();

        for (Rating rating : userRatings) {
            alreadyKnownImdbIds.add(rating.getImdbId());
        }

        for (UserMovie entry : userDiary) {
            alreadyKnownImdbIds.add(entry.getImdbId());
        }

        for (WatchEvent event : userWatchEvents) {
            alreadyKnownImdbIds.add(event.getImdbId());
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
            if (entry.getStatus() == WatchStatus.WATCHED) {
                Movie movie = movieRepo.findByImdbId(entry.getImdbId()).orElse(null);
                if (movie != null && movie.getGenres() != null) {
                    for (Genre genre : movie.getGenres()) {
                        genreScores.put(genre.getName(), genreScores.getOrDefault(genre.getName(), 0) + 1);
                    }
                }
            }
        }

        Collection<Movie> allMovies = movieRepo.findAll();

        for (Movie candidate : allMovies) {

            if (alreadyKnownImdbIds.contains(candidate.getImdbId())) {
                continue;
            }

            if (candidate.getGenres() == null || candidate.getGenres().isEmpty()) {
                continue;
            }

            BigDecimal score = BigDecimal.ZERO;
            StringBuilder reasonBuilder = new StringBuilder();

            for (Genre genre : candidate.getGenres()) {
                Integer genreScore = genreScores.get(genre.getName());
                if (genreScore != null) {
                    score = score.add(BigDecimal.valueOf(genreScore));
                    if (reasonBuilder.length() == 0) {
                        reasonBuilder.append("Because you like ").append(genre.getName());
                    }
                }
            }

            if (candidate.getImdbRating() != null) {
                score = score.add(candidate.getImdbRating().divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP));
            }

            if (score.compareTo(BigDecimal.ZERO) > 0) {
                Recommendation recommendation = new Recommendation(
                        user,
                        candidate,
                        score,
                        reasonBuilder.length() > 0 ? reasonBuilder.toString() : "Based on your movie preferences"
                );
                recommendationRepo.save(recommendation);
            }
        }
    }

    @Override
    public Collection<Recommendation> retrieveByUserId(Long userId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        return recommendationRepo.findByUser(user);
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
}