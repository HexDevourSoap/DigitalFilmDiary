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
	public CommandLineRunner testDataLayer(
			IGenreRepo genreRepo,
			IMovieRepo movieRepo,
			IMyAuthorityRepo authRepo,
			IAppUserRepo userRepo,
			IRatingRepo ratingRepo,
			IUserMovieRepo userMovieRepo,
			IWatchEventRepo watchEventRepo) {

		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {

				// so data is not inserted every restart
				if (genreRepo.count() > 0 || movieRepo.count() > 0 || userRepo.count() > 0) {
					return;
				}

				// -------------------------
				// Authorities
				// -------------------------
				MyAuthority userAuthority = new MyAuthority("ROLE_USER");
				MyAuthority adminAuthority = new MyAuthority("ROLE_ADMIN");

				authRepo.saveAll(Arrays.asList(userAuthority, adminAuthority));

				// -------------------------
				// Users
				// -------------------------
				PasswordEncoder passEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

				AppUser user1 = new AppUser("user1@moviediary.lv", passEncoder.encode("user12345"), userAuthority);
				AppUser user2 = new AppUser("user2@moviediary.lv", passEncoder.encode("user12345"), userAuthority);
				AppUser admin = new AppUser("admin@moviediary.lv", passEncoder.encode("admin12345"), adminAuthority);

				userRepo.saveAll(Arrays.asList(user1, user2, admin));

				// -------------------------
				// Genres
				// -------------------------
				Genre action = new Genre("Action");
				Genre drama = new Genre("Drama");
				Genre sciFi = new Genre("Sci-Fi");
				Genre thriller = new Genre("Thriller");
				Genre comedy = new Genre("Comedy");

				genreRepo.saveAll(Arrays.asList(action, drama, sciFi, thriller, comedy));

				// -------------------------
				// Movies
				// -------------------------
				Movie movie1 = new Movie(
						"tt0133093",
						"The Matrix",
						1999,
						136,
						"A computer hacker learns the truth about reality and his role in the war against its controllers."
				);
				movie1.setPosterUrl("https://m.media-amazon.com/images/M/MV5BNzQzOTk3NTAt.jpg");
				movie1.setGenres(Arrays.asList(action, sciFi));

				Movie movie2 = new Movie(
						"tt1375666",
						"Inception",
						2010,
						148,
						"A thief who enters dreams is given a chance at redemption if he can perform inception."
				);
				movie2.setPosterUrl("https://m.media-amazon.com/images/M/MV5BMmEz.jpg");
				movie2.setGenres(Arrays.asList(action, sciFi, thriller));

				Movie movie3 = new Movie(
						"tt0816692",
						"Interstellar",
						2014,
						169,
						"A team travels through a wormhole in space in an attempt to ensure humanity's survival."
				);
				movie3.setPosterUrl("https://m.media-amazon.com/images/M/MV5BZjdkOTU3.jpg");
				movie3.setGenres(Arrays.asList(drama, sciFi));

				Movie movie4 = new Movie(
						"tt0110912",
						"Pulp Fiction",
						1994,
						154,
						"The lives of several criminals intertwine in a series of violent and darkly comic incidents."
				);
				movie4.setPosterUrl("https://m.media-amazon.com/images/M/MV5BNGNhMDIz.jpg");
				movie4.setGenres(Arrays.asList(drama, thriller, comedy));

				movieRepo.saveAll(Arrays.asList(movie1, movie2, movie3, movie4));

				// -------------------------
				// User diary statuses
				// -------------------------
				UserMovie um1 = new UserMovie(user1, movie1, WatchStatus.WATCHED);
				UserMovie um2 = new UserMovie(user1, movie2, WatchStatus.PLAN_TO_WATCH, LocalDate.now().plusDays(3));
				UserMovie um3 = new UserMovie(user2, movie3, WatchStatus.WATCHING);
				UserMovie um4 = new UserMovie(user2, movie4, WatchStatus.WATCHED);

				userMovieRepo.saveAll(Arrays.asList(um1, um2, um3, um4));

				// -------------------------
				// Ratings
				// -------------------------
				Rating rating1 = new Rating(user1, movie1, 10);
				Rating rating2 = new Rating(user2, movie4, 9);
				Rating rating3 = new Rating(user2, movie3, 8);

				ratingRepo.saveAll(Arrays.asList(rating1, rating2, rating3));

				// -------------------------
				// Watch events
				// -------------------------
				WatchEvent watch1 = new WatchEvent(
						user1,
						movie1,
						LocalDate.now().minusDays(7),
						"Mind-blowing sci-fi. Definitely rewatching."
				);

				WatchEvent watch2 = new WatchEvent(
						user2,
						movie4,
						LocalDate.now().minusDays(2),
						"Great dialogue and very memorable scenes."
				);

				WatchEvent watch3 = new WatchEvent(
						user2,
						movie3,
						LocalDate.now().minusDays(1),
						"Watched half of it, finishing later."
				);

				watchEventRepo.saveAll(Arrays.asList(watch1, watch2, watch3));
			}
		};
	}
}