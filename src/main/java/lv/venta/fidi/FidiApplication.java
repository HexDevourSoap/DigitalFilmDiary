package lv.venta.fidi;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

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
            IWatchEventRepo watchEventRepo) {

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
            // Movies
            // -------------------------
            Movie movie1 = movieRepo.findByImdbId("tt0133093")
                    .orElseGet(() -> {
                        Movie m = new Movie(
                                "tt0133093",
                                "The Matrix",
                                1999,
                                136,
                                "A computer hacker learns the truth about reality and his role in the war against its controllers.");
                        m.setPosterUrl("https://m.media-amazon.com/images/M/MV5BNzQzOTk3NTAt.jpg");
                        m.setGenres(Arrays.asList(action, sciFi));
                        return movieRepo.save(m);
                    });

            Movie movie2 = movieRepo.findByImdbId("tt1375666")
                    .orElseGet(() -> {
                        Movie m = new Movie(
                                "tt1375666",
                                "Inception",
                                2010,
                                148,
                                "A thief who enters dreams is given a chance at redemption if he can perform inception.");
                        m.setPosterUrl("https://m.media-amazon.com/images/M/MV5BMmEz.jpg");
                        m.setGenres(Arrays.asList(action, sciFi, thriller));
                        return movieRepo.save(m);
                    });

            Movie movie3 = movieRepo.findByImdbId("tt0816692")
                    .orElseGet(() -> {
                        Movie m = new Movie(
                                "tt0816692",
                                "Interstellar",
                                2014,
                                169,
                                "A team travels through a wormhole in space in an attempt to ensure humanity's survival.");
                        m.setPosterUrl("https://m.media-amazon.com/images/M/MV5BZjdkOTU3.jpg");
                        m.setGenres(Arrays.asList(drama, sciFi));
                        return movieRepo.save(m);
                    });

            Movie movie4 = movieRepo.findByImdbId("tt0110912")
                    .orElseGet(() -> {
                        Movie m = new Movie(
                                "tt0110912",
                                "Pulp Fiction",
                                1994,
                                154,
                                "The lives of several criminals intertwine in a series of violent and darkly comic incidents.");
                        m.setPosterUrl("https://m.media-amazon.com/images/M/MV5BNGNhMDIz.jpg");
                        m.setGenres(Arrays.asList(drama, thriller, comedy));
                        return movieRepo.save(m);
                    });

            // -------------------------
            // UserMovie diary entries
            // -------------------------
            if (!userMovieRepo.existsByUserAndMovie(user1, movie1)) {
                userMovieRepo.save(new UserMovie(user1, movie1, WatchStatus.WATCHED));
            }

            if (!userMovieRepo.existsByUserAndMovie(user1, movie2)) {
                userMovieRepo.save(new UserMovie(user1, movie2, WatchStatus.PLAN_TO_WATCH, LocalDate.now().plusDays(3)));
            }

            if (!userMovieRepo.existsByUserAndMovie(user2, movie3)) {
                userMovieRepo.save(new UserMovie(user2, movie3, WatchStatus.WATCHING));
            }

            if (!userMovieRepo.existsByUserAndMovie(user2, movie4)) {
                userMovieRepo.save(new UserMovie(user2, movie4, WatchStatus.WATCHED));
            }

            // -------------------------
            // Ratings
            // -------------------------
            if (!ratingRepo.existsByUserAndMovie(user1, movie1)) {
                ratingRepo.save(new Rating(user1, movie1, 10));
            }

            if (!ratingRepo.existsByUserAndMovie(user2, movie4)) {
                ratingRepo.save(new Rating(user2, movie4, 9));
            }

            if (!ratingRepo.existsByUserAndMovie(user2, movie3)) {
                ratingRepo.save(new Rating(user2, movie3, 8));
            }

            // -------------------------
            // Watch events
            // -------------------------
            if (watchEventRepo.findByUserAndMovie(user1, movie1).isEmpty()) {
                watchEventRepo.save(new WatchEvent(
                        user1,
                        movie1,
                        LocalDate.now().minusDays(7),
                        "Mind-blowing sci-fi. Definitely rewatching."));
            }

            if (watchEventRepo.findByUserAndMovie(user2, movie4).isEmpty()) {
                watchEventRepo.save(new WatchEvent(
                        user2,
                        movie4,
                        LocalDate.now().minusDays(2),
                        "Great dialogue and very memorable scenes."));
            }

            if (watchEventRepo.findByUserAndMovie(user2, movie3).isEmpty()) {
                watchEventRepo.save(new WatchEvent(
                        user2,
                        movie3,
                        LocalDate.now().minusDays(1),
                        "Watched half of it, finishing later."));
            }

            System.out.println("Seed data loaded.");
            System.out.println("Login user: user1@moviediary.lv / user12345");
            System.out.println("Login admin: admin@moviediary.lv / admin12345");
        };
    }
}