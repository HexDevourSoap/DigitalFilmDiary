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

            Genre crime = genreRepo.findByName("Crime")
                    .orElseGet(() -> genreRepo.save(new Genre("Crime")));

            Genre adventure = genreRepo.findByName("Adventure")
                    .orElseGet(() -> genreRepo.save(new Genre("Adventure")));

            Genre fantasy = genreRepo.findByName("Fantasy")
                    .orElseGet(() -> genreRepo.save(new Genre("Fantasy")));

            Genre animation = genreRepo.findByName("Animation")
                    .orElseGet(() -> genreRepo.save(new Genre("Animation")));

            Genre family = genreRepo.findByName("Family")
                    .orElseGet(() -> genreRepo.save(new Genre("Family")));

            Genre romance = genreRepo.findByName("Romance")
                    .orElseGet(() -> genreRepo.save(new Genre("Romance")));

            Genre mystery = genreRepo.findByName("Mystery")
                    .orElseGet(() -> genreRepo.save(new Genre("Mystery")));

            Genre western = genreRepo.findByName("Western")
                    .orElseGet(() -> genreRepo.save(new Genre("Western")));

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
                    Arrays.asList(drama, thriller, crime));

            Movie movie5 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0468569",
                    Arrays.asList(action, crime, drama, thriller));

            Movie movie6 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0068646",
                    Arrays.asList(crime, drama));

            Movie movie7 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0111161",
                    Arrays.asList(drama));

            Movie movie8 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0109830",
                    Arrays.asList(drama, romance));

            Movie movie9 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1392190",
                    Arrays.asList(action, adventure, sciFi, thriller));

            Movie movie10 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt7286456",
                    Arrays.asList(crime, drama, thriller));

            Movie movie11 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1853728",
                    Arrays.asList(action, drama));

            Movie movie12 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt4154756",
                    Arrays.asList(action, adventure, sciFi));

            Movie movie13 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt4154796",
                    Arrays.asList(action, adventure, drama, sciFi));

            Movie movie14 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0903747",
                    Arrays.asList(crime, drama, thriller));

            Movie movie15 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0126029",
                    Arrays.asList(adventure, animation, comedy, family, fantasy));

            Movie movie16 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0298148",
                    Arrays.asList(adventure, animation, comedy, family, fantasy));

            Movie movie17 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0413267",
                    Arrays.asList(adventure, animation, comedy, family, fantasy));

            Movie movie18 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0892791",
                    Arrays.asList(adventure, animation, comedy, family, fantasy));

            Movie movie19 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt2380307",
                    Arrays.asList(animation, adventure, comedy, drama, family, fantasy));

            Movie movie20 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0472033",
                    Arrays.asList(action, adventure, sciFi));

            Movie movie21 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1630029",
                    Arrays.asList(animation, adventure, comedy, family, fantasy));

            Movie movie22 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1825683",
                    Arrays.asList(action, adventure, drama, sciFi));

            Movie movie23 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1517268",
                    Arrays.asList(adventure, comedy, fantasy));

            Movie movie24 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1099212",
                    Arrays.asList(action, adventure, sciFi));

            Movie movie25 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0482571",
                    Arrays.asList(drama, mystery, sciFi, thriller));

            Movie movie26 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0137523",
                    Arrays.asList(drama));

            Movie movie27 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0114369",
                    Arrays.asList(crime, drama, mystery, thriller));

            Movie movie28 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0102926",
                    Arrays.asList(crime, drama, thriller));

            Movie movie29 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0172495",
                    Arrays.asList(action, adventure, drama));

            Movie movie30 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0120815",
                    Arrays.asList(drama));

            Movie movie31 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0167260",
                    Arrays.asList(action, adventure, drama, fantasy));

            Movie movie32 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0167261",
                    Arrays.asList(action, adventure, drama, fantasy));

            Movie movie33 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0167263",
                    Arrays.asList(action, adventure, drama, fantasy));

            Movie movie34 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt2582802",
                    Arrays.asList(crime, drama));

            Movie movie35 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0118799",
                    Arrays.asList(comedy, drama, romance));

            Movie movie36 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0110357",
                    Arrays.asList(adventure, animation, drama, family));

            Movie movie37 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0266543",
                    Arrays.asList(adventure, animation, comedy, family));

            Movie movie38 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0317705",
                    Arrays.asList(adventure, animation, comedy, family));

            Movie movie39 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0114709",
                    Arrays.asList(adventure, animation, comedy, family, fantasy));

            Movie movie40 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0435761",
                    Arrays.asList(adventure, animation, comedy, family, fantasy));

            Movie movie41 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0099685",
                    Arrays.asList(action, adventure, drama, western));

            Movie movie42 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0209144",
                    Arrays.asList(mystery, thriller));

            Movie movie43 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0114814",
                    Arrays.asList(crime, mystery, thriller));

            Movie movie44 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1345836",
                    Arrays.asList(action, drama, thriller));

            Movie movie45 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1130884",
                    Arrays.asList(mystery, thriller));

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

            if (!userMovieRepo.existsByUserAndImdbId(user1, movie15.getImdbId())) {
                userMovieRepo.save(new UserMovie(user1, movie15.getImdbId(), WatchStatus.WATCHED));
            }

            if (!userMovieRepo.existsByUserAndImdbId(user2, movie20.getImdbId())) {
                userMovieRepo.save(new UserMovie(user2, movie20.getImdbId(), WatchStatus.PLAN_TO_WATCH));
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

            if (!ratingRepo.existsByUserAndImdbId(user1, movie15.getImdbId())) {
                ratingRepo.save(new Rating(user1, movie15.getImdbId(), 8));
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