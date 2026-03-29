package lv.venta.fidi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Genre;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.MyAuthority;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.model.WatchEvent;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IGenreRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.repo.IMyAuthorityRepo;
import lv.venta.fidi.repo.IRatingRepo;
import lv.venta.fidi.repo.IUserMovieRepo;
import lv.venta.fidi.repo.IWatchEventRepo;
import lv.venta.fidi.service.OmdbClient;

@SpringBootApplication
public class FidiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FidiApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(
            IGenreRepo genreRepo,
            IMovieRepo movieRepo,
            IMyAuthorityRepo authorityRepo,
            IAppUserRepo userRepo,
            IRatingRepo ratingRepo,
            IUserMovieRepo userMovieRepo,
            IWatchEventRepo watchEventRepo,
            OmdbClient omdbClient) {

        return args -> {

            PasswordEncoder passEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

            // -------------------------
            // Authorities
            // -------------------------
            MyAuthority userRole = authorityRepo.findByTitle("ROLE_USER")
                    .orElseGet(() -> authorityRepo.save(new MyAuthority("ROLE_USER")));

            MyAuthority adminRole = authorityRepo.findByTitle("ROLE_ADMIN")
                    .orElseGet(() -> authorityRepo.save(new MyAuthority("ROLE_ADMIN")));

            // -------------------------
            // Users
            // -------------------------
            AppUser user1 = userRepo.findByEmail("user1@moviediary.lv")
                    .orElseGet(() -> userRepo.save(
                            new AppUser("user1@moviediary.lv", passEncoder.encode("user12345"), userRole)));

            AppUser user2 = userRepo.findByEmail("user2@moviediary.lv")
                    .orElseGet(() -> userRepo.save(
                            new AppUser("user2@moviediary.lv", passEncoder.encode("user12345"), userRole)));

            AppUser admin = userRepo.findByEmail("admin@moviediary.lv")
                    .orElseGet(() -> userRepo.save(
                            new AppUser("admin@moviediary.lv", passEncoder.encode("admin12345"), adminRole)));

            // -------------------------
            // Genres
            // -------------------------
            Genre action = genreRepo.findByName("Action")
                    .orElseGet(() -> genreRepo.save(new Genre("Action")));

            Genre drama = genreRepo.findByName("Drama")
                    .orElseGet(() -> genreRepo.save(new Genre("Drama")));

            Genre sciFi = genreRepo.findByName("Sci-Fi")
                    .orElseGet(() -> genreRepo.save(new Genre("Sci-Fi")));

            Genre thriller = genreRepo.findByName("Thriller")
                    .orElseGet(() -> genreRepo.save(new Genre("Thriller")));

            Genre comedy = genreRepo.findByName("Comedy")
                    .orElseGet(() -> genreRepo.save(new Genre("Comedy")));

            // -------------------------
            // Movies cache / metadata from OMDb
            // -------------------------
            Movie movie1 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0133093",
                    Arrays.asList(action, sciFi));

            Movie movie2 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1375666",
                    Arrays.asList(action, sciFi, thriller));

            Movie movie3 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0816692",
                    Arrays.asList(drama, sciFi));

            Movie movie4 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0110912",
                    Arrays.asList(drama, thriller, comedy));

            // -------------------------
            // UserMovie diary entries
            // -------------------------
            if (!userMovieRepo.existsByUserAndImdbId(user1, movie1.getImdbId())) {
                userMovieRepo.save(new UserMovie(user1, movie1.getImdbId(), WatchStatus.WATCHED));
            }

            if (!userMovieRepo.existsByUserAndImdbId(user1, movie2.getImdbId())) {
                userMovieRepo.save(new UserMovie(
                        user1,
                        movie2.getImdbId(),
                        WatchStatus.PLAN_TO_WATCH,
                        LocalDate.now().plusDays(3)
                ));
            }

            if (!userMovieRepo.existsByUserAndImdbId(user2, movie3.getImdbId())) {
                userMovieRepo.save(new UserMovie(user2, movie3.getImdbId(), WatchStatus.WATCHING));
            }

            if (!userMovieRepo.existsByUserAndImdbId(user2, movie4.getImdbId())) {
                userMovieRepo.save(new UserMovie(user2, movie4.getImdbId(), WatchStatus.WATCHED));
            }

            // -------------------------
            // Ratings
            // -------------------------
            if (!ratingRepo.existsByUserAndImdbId(user1, movie1.getImdbId())) {
                ratingRepo.save(new Rating(user1, movie1.getImdbId(), 10));
            }

            if (!ratingRepo.existsByUserAndImdbId(user2, movie4.getImdbId())) {
                ratingRepo.save(new Rating(user2, movie4.getImdbId(), 9));
            }

            if (!ratingRepo.existsByUserAndImdbId(user2, movie3.getImdbId())) {
                ratingRepo.save(new Rating(user2, movie3.getImdbId(), 8));
            }

            // -------------------------
            // Watch events
            // -------------------------
            if (watchEventRepo.findByUserAndImdbId(user1, movie1.getImdbId()).isEmpty()) {
                watchEventRepo.save(new WatchEvent(
                        user1,
                        movie1.getImdbId(),
                        LocalDate.now().minusDays(7),
                        "Mind-blowing sci-fi. Definitely rewatching."
                ));
            }

            if (watchEventRepo.findByUserAndImdbId(user2, movie4.getImdbId()).isEmpty()) {
                watchEventRepo.save(new WatchEvent(
                        user2,
                        movie4.getImdbId(),
                        LocalDate.now().minusDays(2),
                        "Great dialogue and very memorable scenes."
                ));
            }

            if (watchEventRepo.findByUserAndImdbId(user2, movie3.getImdbId()).isEmpty()) {
                watchEventRepo.save(new WatchEvent(
                        user2,
                        movie3.getImdbId(),
                        LocalDate.now().minusDays(1),
                        "Watched half of it, finishing later."
                ));
            }

            System.out.println("Seed data loaded.");
            System.out.println("Login user: user1@moviediary.lv / user12345");
            System.out.println("Login admin: admin@moviediary.lv / admin12345");
        };
    }

    private Movie getOrUpdateMovieFromOmdb(
            IMovieRepo movieRepo,
            OmdbClient omdbClient,
            String imdbId,
            Collection<Genre> genres) throws Exception {

        OmdbMovieDto dto = omdbClient.getByImdbId(imdbId);

        if (dto == null || dto.getImdbID() == null || dto.getImdbID().isBlank()) {
            throw new Exception("Could not load movie from OMDb for IMDb ID: " + imdbId);
        }

        Movie movie = movieRepo.findByImdbId(imdbId)
                .orElse(new Movie(
                        dto.getImdbID(),
                        dto.getTitle(),
                        null,
                        null,
                        dto.getPlot()
                ));

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getPlot());
        movie.setPosterUrl("N/A".equals(dto.getPoster()) ? null : dto.getPoster());
        movie.setReleaseYear(parseYear(dto.getYear()));
        movie.setRuntimeMin(parseRuntime(dto.getRuntime()));
        movie.setImdbRating(parseRating(dto.getImdbRating()));
        movie.setGenres(genres);

        return movieRepo.save(movie);
    }

    private Integer parseYear(String year) {
        try {
            if (year != null && !year.isBlank()) {
                String cleaned = year.replaceAll("[^0-9]", "");
                if (cleaned.length() >= 4) {
                    return Integer.parseInt(cleaned.substring(0, 4));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Integer parseRuntime(String runtime) {
        try {
            if (runtime != null && !runtime.isBlank()) {
                String cleaned = runtime.replaceAll("[^0-9]", "");
                if (!cleaned.isBlank()) {
                    return Integer.parseInt(cleaned);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private BigDecimal parseRating(String imdbRating) {
        try {
            if (imdbRating != null && !imdbRating.isBlank() && !"N/A".equals(imdbRating)) {
                return new BigDecimal(imdbRating);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}