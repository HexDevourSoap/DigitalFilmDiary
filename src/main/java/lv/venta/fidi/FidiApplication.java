package lv.venta.fidi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    @Autowired
    private IGenreRepo genreRepo;

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

            Genre biography = genreRepo.findByName("Biography")
                    .orElseGet(() -> genreRepo.save(new Genre("Biography")));

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
                    Arrays.asList(action, drama, western));

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
                    Arrays.asList(crime, drama));

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

            Movie movie46 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0050083",
                    Arrays.asList(crime, drama));

            Movie movie47 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0086250",
                    Arrays.asList(crime, drama, mystery));

            Movie movie48 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1832382",
                    Arrays.asList(drama, mystery, thriller));

            Movie movie49 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0361748",
                    Arrays.asList(adventure, drama));

            Movie movie50 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt10872600",
                    Arrays.asList(drama, mystery, sciFi));

            Movie movie51 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0120735",
                    Arrays.asList(action, adventure, fantasy));

            Movie movie52 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0076759",
                    Arrays.asList(action, adventure, fantasy, sciFi));

            Movie movie53 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt6751668",
                    Arrays.asList(comedy, drama, family));

            Movie movie54 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0103064",
                    Arrays.asList(crime, drama, thriller));

            Movie movie55 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt5074352",
                    Arrays.asList(biography, drama));

            Movie movie56 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt7131622",
                    Arrays.asList(comedy, drama));

            Movie movie57 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0112573",
                    Arrays.asList(biography, drama));

            Movie movie58 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0093058",
                    Arrays.asList(crime, mystery, thriller));

            Movie movie59 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0110413",
                    Arrays.asList(action, crime, drama, thriller));

            Movie movie60 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0993846",
                    Arrays.asList(biography, comedy, crime, drama));

            Movie movie61 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0073486",
                    Arrays.asList(crime, drama, mystery));

            Movie movie62 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0407887",
                    Arrays.asList(crime, drama, mystery));

            Movie movie63 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt8503618",
                    Arrays.asList(crime, drama, mystery, thriller));

            Movie movie64 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt6966692",
                    Arrays.asList(comedy, drama));

            Movie movie65 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt5027774",
                    Arrays.asList(crime, drama));

            Movie movie66 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1160419",
                    Arrays.asList(action, adventure, drama, sciFi));

            Movie movie67 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt4154664",
                    Arrays.asList(action, adventure, comedy, sciFi));

            Movie movie68 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt9362722",
                    Arrays.asList(action, comedy, crime, thriller));

            Movie movie69 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0118715",
                    Arrays.asList(comedy, crime));

            Movie movie70 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt2278388",
                    Arrays.asList(comedy, drama, mystery));

            Movie movie71 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0454921",
                    Arrays.asList(adventure, family, fantasy, mystery));

            Movie movie72 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1201607",
                    Arrays.asList(adventure, family, fantasy, mystery));

            Movie movie74 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt10272386",
                    Arrays.asList(adventure, comedy, family, fantasy, mystery));

            Movie movie75 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1187043",
                    Arrays.asList(comedy, drama, romance));

            // Western pack
            Movie movie76 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0060196",
                    Arrays.asList(western));

            Movie movie77 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0064116",
                    Arrays.asList(western));

            Movie movie78 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0105695",
                    Arrays.asList(western, drama));

            Movie movie79 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt1403865",
                    Arrays.asList(western, drama));

            Movie movie80 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt2404435",
                    Arrays.asList(action, western));

            Movie movie81 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0054047",
                    Arrays.asList(action, adventure, western));

            Movie movie82 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0071230",
                    Arrays.asList(crime, western));

            Movie movie83 = getOrUpdateMovieFromOmdb(
                    movieRepo, omdbClient,
                    "tt0120689",
                    Arrays.asList(fantasy, western));

            // Extra catalog pack to increase depth across all genres
            List<String> extraImdbIds = List.of(
                    "tt0120737", // LOTR: Fellowship
                    "tt0167261", // LOTR: Two Towers
                    "tt0167260", // LOTR: Return of the King
                    "tt0372784", // Batman Begins
                    "tt1877830", // The Batman
                    "tt0088763", // Back to the Future
                    "tt0082971", // Raiders of the Lost Ark
                    "tt0369610", // Jurassic World
                    "tt0398286", // Tangled
                    "tt0245429", // Spirited Away
                    "tt0347149", // Howl's Moving Castle
                    "tt1981115", // Thor: The Dark World
                    "tt2395427", // Avengers: Age of Ultron
                    "tt0848228", // The Avengers
                    "tt2015381", // Guardians of the Galaxy
                    "tt4154796", // Avengers: Endgame
                    "tt5726616", // Call Me by Your Name
                    "tt2582846", // The Fault in Our Stars
                    "tt0332280", // The Notebook
                    "tt0105236", // Reservoir Dogs
                    "tt0120586", // American History X
                    "tt0405094", // The Lives of Others
                    "tt0910970", // WALL-E
                    "tt1049413", // Up
                    "tt1979376", // Toy Story 4
                    "tt2096673", // Inside Out
                    "tt2948372", // Soul
                    "tt4633694", // Spider-Man: Into the Spider-Verse
                    "tt2382320", // No Time to Die
                    "tt0478970", // Ant-Man
                    "tt6723592", // Tenet
                    "tt0120382", // The Truman Show
                    "tt0107048", // Groundhog Day
                    "tt0264464", // Catch Me If You Can
                    "tt8579674", // 1917
                    "tt2119532", // Hacksaw Ridge
                    "tt0108052", // Schindler's List
                    "tt0078788", // Apocalypse Now
                    "tt1856101", // Blade Runner 2049
                    "tt0083658", // Blade Runner
                    "tt1371111", // Edge of Tomorrow
                    "tt4154664", // Captain Marvel
                    "tt3498820", // Captain America: Civil War
                    "tt3896198", // Guardians of the Galaxy Vol. 2
                    "tt3521164", // Moana
                    "tt2948356", // Zootopia
                    "tt0844471", // Cloudy with a Chance of Meatballs
                    "tt0095327", // Grave of the Fireflies
                    "tt0180093", // Requiem for a Dream
                    "tt0095765", // Cinema Paradiso
                    "tt0434409", // V for Vendetta
                    "tt0118715", // The Big Lebowski
                    "tt2267998", // Gone Girl
                    "tt1170358", // The Hobbit
                    "tt0903624", // The Hobbit: Desolation of Smaug
                    "tt2310332", // The Hobbit: Battle of Five Armies
                    "tt0043014", // Sunset Boulevard
                    "tt0053221", // Rio Bravo
                    "tt0062622", // Butch Cassidy and the Sundance Kid
                    // Horror-heavy pack
                    "tt0081505", // The Shining
                    "tt6751668", // Parasite (thriller/drama, sometimes misc)
                    "tt0070047", // The Exorcist
                    "tt0102926", // The Silence of the Lambs
                    "tt1457767", // The Conjuring
                    "tt1179904", // Insidious
                    "tt7784604", // Hereditary
                    "tt5052448", // Get Out
                    "tt1396484", // It
                    "tt0087800", // A Nightmare on Elm Street
                    "tt0078748", // Alien
                    "tt0120601", // Being John Malkovich (fallback genre mix)
                    "tt1051906", // The Invisible Man
                    "tt3387520", // The Witch
                    "tt2717822", // It Follows
                    "tt4912910", // Mission: Impossible Fallout (action)
                    "tt0816692", // Interstellar duplicate safe
                    "tt0266697", // Kill Bill vol 1
                    "tt0166924", // Mulholland Drive
                    "tt0117571", // Scream
                    "tt0387564", // Saw
                    "tt0073195", // Jaws
                    "tt0325980", // Pirates of the Caribbean
                    "tt0338013", // Eternal Sunshine
                    "tt2106476", // The Hunt
                    "tt0090605", // Aliens
                    "tt0133093", // Matrix duplicate safe
                    "tt0119488", // L.A. Confidential
                    "tt0105236", // Reservoir Dogs duplicate safe
                    "tt8579674", // 1917 duplicate safe
                    "tt0266697"  // Kill Bill vol 1
            );

            for (String extraImdbId : extraImdbIds) {
                try {
                    getOrUpdateMovieFromOmdb(movieRepo, omdbClient, extraImdbId, Arrays.asList(drama));
                } catch (Exception ignored) {
                    // Skip invalid/unavailable IDs; keep seeding resilient.
                }
            }

            ensureMinimumMoviesPerGenre(movieRepo, omdbClient, 10);

            // -------------------------
            // UserMovie diary entries
            // -------------------------
            if (movie1 != null && !userMovieRepo.existsByUserAndImdbId(user1, movie1.getImdbId())) {
                userMovieRepo.save(new UserMovie(user1, movie1.getImdbId(), WatchStatus.WATCHED));
            }

            if (movie2 != null && !userMovieRepo.existsByUserAndImdbId(user1, movie2.getImdbId())) {
                userMovieRepo.save(new UserMovie(
                        user1,
                        movie2.getImdbId(),
                        WatchStatus.PLAN_TO_WATCH,
                        LocalDate.now().plusDays(3)
                ));
            }

            if (movie3 != null && !userMovieRepo.existsByUserAndImdbId(user2, movie3.getImdbId())) {
                userMovieRepo.save(new UserMovie(user2, movie3.getImdbId(), WatchStatus.WATCHING));
            }

            if (movie4 != null && !userMovieRepo.existsByUserAndImdbId(user2, movie4.getImdbId())) {
                userMovieRepo.save(new UserMovie(user2, movie4.getImdbId(), WatchStatus.WATCHED));
            }

            if (movie15 != null && !userMovieRepo.existsByUserAndImdbId(user1, movie15.getImdbId())) {
                userMovieRepo.save(new UserMovie(user1, movie15.getImdbId(), WatchStatus.WATCHED));
            }

            if (movie20 != null && !userMovieRepo.existsByUserAndImdbId(user2, movie20.getImdbId())) {
                userMovieRepo.save(new UserMovie(user2, movie20.getImdbId(), WatchStatus.PLAN_TO_WATCH));
            }

            // -------------------------
            // Ratings
            // -------------------------
            if (movie1 != null && !ratingRepo.existsByUserAndImdbId(user1, movie1.getImdbId())) {
                ratingRepo.save(new Rating(user1, movie1.getImdbId(), 10));
            }

            if (movie4 != null && !ratingRepo.existsByUserAndImdbId(user2, movie4.getImdbId())) {
                ratingRepo.save(new Rating(user2, movie4.getImdbId(), 9));
            }

            if (movie3 != null && !ratingRepo.existsByUserAndImdbId(user2, movie3.getImdbId())) {
                ratingRepo.save(new Rating(user2, movie3.getImdbId(), 8));
            }

            if (movie15 != null && !ratingRepo.existsByUserAndImdbId(user1, movie15.getImdbId())) {
                ratingRepo.save(new Rating(user1, movie15.getImdbId(), 8));
            }

            // -------------------------
            // Watch events
            // -------------------------
            if (movie1 != null && watchEventRepo.findByUserAndImdbId(user1, movie1.getImdbId()).isEmpty()) {
                watchEventRepo.save(new WatchEvent(
                        user1,
                        movie1.getImdbId(),
                        LocalDate.now().minusDays(7),
                        "Mind-blowing sci-fi. Definitely rewatching."
                ));
            }

            if (movie4 != null && watchEventRepo.findByUserAndImdbId(user2, movie4.getImdbId()).isEmpty()) {
                watchEventRepo.save(new WatchEvent(
                        user2,
                        movie4.getImdbId(),
                        LocalDate.now().minusDays(2),
                        "Great dialogue and very memorable scenes."
                ));
            }

            if (movie3 != null && watchEventRepo.findByUserAndImdbId(user2, movie3.getImdbId()).isEmpty()) {
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
            Collection<Genre> fallbackGenres) throws Exception {

        Movie existingMovie = movieRepo.findByImdbId(imdbId).orElse(null);
        OmdbMovieDto dto = omdbClient.getByImdbId(imdbId);
        if (dto == null || dto.getImdbID() == null || dto.getImdbID().isBlank()) {
            return existingMovie;
        }

        Movie movie = existingMovie != null
                ? existingMovie
                : new Movie(
                        dto.getImdbID(),
                        dto.getTitle(),
                        null,
                        null,
                        dto.getPlot()
                );

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getPlot());
        movie.setPosterUrl("N/A".equals(dto.getPoster()) ? null : dto.getPoster());
        movie.setReleaseYear(parseYear(dto.getYear()));
        movie.setRuntimeMin(parseRuntime(dto.getRuntime()));
        movie.setImdbRating(parseRating(dto.getImdbRating()));
        movie.setGenres(resolveGenresFromOmdb(dto.getGenre(), fallbackGenres));

        return movieRepo.save(movie);
    }

    private Collection<Genre> resolveGenresFromOmdb(String omdbGenres, Collection<Genre> fallbackGenres) {
        List<Genre> resolved = new ArrayList<>();

        if (omdbGenres != null && !omdbGenres.isBlank() && !"N/A".equalsIgnoreCase(omdbGenres)) {
            String[] names = omdbGenres.split(",");
            for (String raw : names) {
                String name = raw.trim();
                if (name.isEmpty()) {
                    continue;
                }
                Genre genre = genreRepo.findByName(name)
                        .orElseGet(() -> genreRepo.save(new Genre(name)));
                resolved.add(genre);
            }
        }

        if (!resolved.isEmpty()) {
            return resolved;
        }

        return fallbackGenres;
    }

    private void ensureMinimumMoviesPerGenre(IMovieRepo movieRepo, OmdbClient omdbClient, int minimum) {
        Map<String, List<String>> genreQueries = new HashMap<>();
        genreQueries.put("Action", List.of("action", "spy action", "superhero"));
        genreQueries.put("Adventure", List.of("adventure", "treasure", "expedition"));
        genreQueries.put("Animation", List.of("animation", "pixar", "disney animated"));
        genreQueries.put("Biography", List.of("biography", "based on true story", "biopic"));
        genreQueries.put("Comedy", List.of("comedy", "funny movie", "romcom"));
        genreQueries.put("Crime", List.of("crime", "mafia", "detective"));
        genreQueries.put("Drama", List.of("drama", "award winning drama"));
        genreQueries.put("Family", List.of("family movie", "kids movie", "disney"));
        genreQueries.put("Fantasy", List.of("fantasy", "wizard", "magic"));
        genreQueries.put("History", List.of("history", "historical drama", "period film"));
        genreQueries.put("Horror", List.of("horror", "slasher", "haunted"));
        genreQueries.put("Music", List.of("music", "concert film", "musician"));
        genreQueries.put("Musical", List.of("musical", "broadway", "song and dance"));
        genreQueries.put("Mystery", List.of("mystery", "whodunit", "detective thriller"));
        genreQueries.put("Romance", List.of("romance", "love story", "romantic drama"));
        genreQueries.put("Sci-Fi", List.of("sci-fi", "science fiction", "space"));
        genreQueries.put("Sport", List.of("sport", "boxing", "football"));
        genreQueries.put("Thriller", List.of("thriller", "psychological thriller", "suspense"));
        genreQueries.put("War", List.of("war", "world war", "military"));
        genreQueries.put("Western", List.of("western", "cowboy", "wild west"));

        for (Genre genre : genreRepo.findAll()) {
            long count = movieRepo.findByGenresGenreId(genre.getGenreId(), PageRequest.of(0, 1)).getTotalElements();
            if (count >= minimum) {
                continue;
            }

            List<String> queries = genreQueries.getOrDefault(genre.getName(), List.of(genre.getName()));
            Set<String> seen = new HashSet<>();

            for (String query : queries) {
                if (count >= minimum) {
                    break;
                }
                List<lv.venta.fidi.dto.OmdbSearchItemDto> searchResults;
                try {
                    searchResults = omdbClient.searchByTitle(query);
                } catch (Exception ex) {
                    // If OMDb limits are reached, skip top-up for this genre.
                    break;
                }
                for (var item : searchResults) {
                    if (count >= minimum) {
                        break;
                    }
                    if (item == null || item.getImdbID() == null || item.getImdbID().isBlank()) {
                        continue;
                    }
                    if ("series".equalsIgnoreCase(item.getType())) {
                        continue;
                    }
                    if (!seen.add(item.getImdbID())) {
                        continue;
                    }
                    try {
                        OmdbMovieDto dto = omdbClient.getByImdbId(item.getImdbID());
                        if (dto == null || !containsGenre(dto.getGenre(), genre.getName())) {
                            continue;
                        }
                        getOrUpdateMovieFromOmdb(movieRepo, omdbClient, item.getImdbID(), Arrays.asList(genre));
                        count = movieRepo.findByGenresGenreId(genre.getGenreId(), PageRequest.of(0, 1)).getTotalElements();
                    } catch (Exception ignored) {
                        // Continue trying other candidates
                    }
                }
            }
        }
    }

    private boolean containsGenre(String omdbGenreCsv, String genreName) {
        if (omdbGenreCsv == null || omdbGenreCsv.isBlank() || genreName == null || genreName.isBlank()) {
            return false;
        }
        String target = genreName.trim().toLowerCase();
        String[] genres = omdbGenreCsv.split(",");
        for (String g : genres) {
            if (target.equals(g.trim().toLowerCase())) {
                return true;
            }
        }
        return false;
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