package lv.venta.fidi.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.model.Genre;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.repo.IGenreRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.service.IMovieService;
import lv.venta.fidi.service.OmdbClient;

@Service
public class MovieServiceImpl implements IMovieService {

    @Autowired
    private IMovieRepo movieRepo;

    @Autowired
    private IGenreRepo genreRepo;

    @Autowired
    private OmdbClient omdbClient;

    private static final Set<String> STOP_WORDS = Set.of("the", "a", "an", "of");

    @Override
    public List<Movie> getAllMovies() throws Exception {
        return movieRepo.findAll();
    }

    @Override
    public List<Movie> getHomeTrendingPreview(int limit) throws Exception {
        if (limit < 1) {
            return List.of();
        }
        return movieRepo.findAll(PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "imdbRating").and(Sort.by(Sort.Direction.DESC, "movieId"))
        )).getContent();
    }

    @Override
    public Movie getMovieById(Long id) throws Exception {
        if (id == null || id < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        return movieRepo.findById(id)
                .orElseThrow(() -> new Exception("Movie with ID " + id + " was not found"));
    }

    @Override
    public Movie getOrCreateByImdbId(String imdbId) throws Exception {
        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        imdbId = imdbId.trim();
        Movie existingMovie = movieRepo.findByImdbId(imdbId).orElse(null);

        OmdbMovieDto dto = omdbClient.getByImdbId(imdbId);

        if (dto == null || dto.getImdbID() == null || dto.getImdbID().isBlank()) {
            if (existingMovie != null) {
                return existingMovie;
            }
            throw new Exception("Movie was not found in OMDb");
        }

        Integer year = null;
        Integer runtime = null;
        BigDecimal imdbRating = null;

        try {
            if (dto.getYear() != null && !dto.getYear().isBlank()) {
                String cleanedYear = dto.getYear().replaceAll("[^0-9]", "");
                if (cleanedYear.length() >= 4) {
                    year = Integer.parseInt(cleanedYear.substring(0, 4));
                }
            }
        } catch (Exception e) {
            year = null;
        }

        try {
            if (dto.getRuntime() != null && !dto.getRuntime().isBlank()) {
                String cleanedRuntime = dto.getRuntime().replaceAll("[^0-9]", "");
                if (!cleanedRuntime.isBlank()) {
                    runtime = Integer.parseInt(cleanedRuntime);
                }
            }
        } catch (Exception e) {
            runtime = null;
        }

        try {
            if (dto.getImdbRating() != null
                    && !dto.getImdbRating().isBlank()
                    && !"N/A".equals(dto.getImdbRating())) {
                imdbRating = new BigDecimal(dto.getImdbRating());
            }
        } catch (Exception e) {
            imdbRating = null;
        }

        Movie movie = existingMovie != null
                ? existingMovie
                : new Movie(
                        dto.getImdbID(),
                        dto.getTitle(),
                        year,
                        runtime,
                        dto.getPlot()
                );

        movie.setImdbId(dto.getImdbID());
        movie.setTitle(dto.getTitle());
        movie.setReleaseYear(year);
        movie.setRuntimeMin(runtime);
        movie.setDescription(dto.getPlot());
        movie.setPosterUrl("N/A".equals(dto.getPoster()) ? null : dto.getPoster());
        movie.setImdbRating(imdbRating);

        if (dto.getGenre() != null && !dto.getGenre().isBlank() && !"N/A".equals(dto.getGenre())) {
            List<Genre> movieGenres = new ArrayList<>();
            String[] genreNames = dto.getGenre().split(",");

            for (String genreName : genreNames) {
                String trimmed = genreName.trim();
                if (!trimmed.isBlank()) {
                    Genre genre = genreRepo.findByName(trimmed)
                            .orElseGet(() -> genreRepo.save(new Genre(trimmed)));
                    movieGenres.add(genre);
                }
            }

            movie.setGenres(movieGenres);
        }

        return movieRepo.save(movie);
    }

    @Override
    public List<Movie> searchLocalMoviesFuzzy(String query) throws Exception {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }

        String normalizedQuery = normalize(query);
        if (normalizedQuery.isBlank()) {
            return new ArrayList<>();
        }

        List<Movie> allMovies = movieRepo.findAll();
        List<ScoredMovie> scoredMovies = new ArrayList<>();

        for (Movie movie : allMovies) {
            if (movie.getTitle() == null || movie.getTitle().isBlank()) {
                continue;
            }

            int score = scoreTitleMatch(normalizedQuery, movie.getTitle());

            if (score >= 20) {
                scoredMovies.add(new ScoredMovie(movie, score));
            }
        }

        scoredMovies.sort(Comparator.comparingInt(ScoredMovie::score).reversed());

        List<Movie> result = new ArrayList<>();
        for (ScoredMovie scoredMovie : scoredMovies) {
            result.add(scoredMovie.movie());
        }

        return result;
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        String cleaned = text.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ");

        String[] words = cleaned.split(" ");
        List<String> filtered = new ArrayList<>();

        for (String word : words) {
            if (!word.isBlank() && !STOP_WORDS.contains(word)) {
                filtered.add(word);
            }
        }

        return String.join(" ", filtered).trim();
    }

    private int scoreTitleMatch(String normalizedQuery, String title) {
        String normalizedTitle = normalize(title);

        if (normalizedQuery.isBlank() || normalizedTitle.isBlank()) {
            return 0;
        }

        int score = 0;

        if (normalizedTitle.equals(normalizedQuery)) {
            return 200;
        }

        if (normalizedTitle.contains(normalizedQuery)) {
            score += 90;
        }

        if (normalizedQuery.contains(normalizedTitle)) {
            score += 70;
        }

        String[] queryWords = normalizedQuery.split(" ");
        String[] titleWords = normalizedTitle.split(" ");

        for (String queryWord : queryWords) {
            if (queryWord.isBlank()) {
                continue;
            }

            for (String titleWord : titleWords) {
                if (titleWord.isBlank()) {
                    continue;
                }

                if (queryWord.equals(titleWord)) {
                    score += 60;
                    continue;
                }

                if (titleWord.startsWith(queryWord) || queryWord.startsWith(titleWord)) {
                    score += 35;
                }

                if (titleWord.contains(queryWord) || queryWord.contains(titleWord)) {
                    score += 20;
                }

                int distance = levenshteinDistance(queryWord, titleWord);
                int maxLength = Math.max(queryWord.length(), titleWord.length());

                if (maxLength > 0) {
                    double similarity = 1.0 - ((double) distance / maxLength);

                    if (similarity >= 0.90) {
                        score += 55;
                    } else if (similarity >= 0.80) {
                        score += 40;
                    } else if (similarity >= 0.70) {
                        score += 25;
                    } else if (similarity >= 0.60) {
                        score += 12;
                    }
                }
            }
        }

        int fullDistance = levenshteinDistance(normalizedQuery, normalizedTitle);
        int fullMaxLength = Math.max(normalizedQuery.length(), normalizedTitle.length());

        if (fullMaxLength > 0) {
            double fullSimilarity = 1.0 - ((double) fullDistance / fullMaxLength);

            if (fullSimilarity >= 0.90) {
                score += 60;
            } else if (fullSimilarity >= 0.80) {
                score += 40;
            } else if (fullSimilarity >= 0.70) {
                score += 25;
            } else if (fullSimilarity >= 0.60) {
                score += 10;
            }
        }

        if (queryWords.length > 1 && appearInOrder(queryWords, titleWords)) {
            score += 35;
        }

        return score;
    }

    private boolean appearInOrder(String[] queryWords, String[] titleWords) {
        int index = 0;

        for (String titleWord : titleWords) {
            if (index < queryWords.length && titleWord.contains(queryWords[index])) {
                index++;
            }
        }

        return index == queryWords.length;
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[a.length()][b.length()];
    }

    private record ScoredMovie(Movie movie, int score) {
    }
}