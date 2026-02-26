package lv.venta.fidi.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.service.OmdbClient;

@Component
@RequiredArgsConstructor
public class MovieSeeder implements CommandLineRunner {

    private final OmdbClient omdbClient;
    private final IMovieRepo movieRepository;

    @Override
    public void run(String... args) {

        List<String> imdbIds = List.of(
                "tt0111161", // Shawshank
                "tt0068646", // Godfather
                "tt0468569", // Dark Knight
                "tt0109830", // Forrest Gump
                "tt0137523"  // Fight Club
        );

        for (String imdbId : imdbIds) {

            if (movieRepository.existsByImdbId(imdbId)) {
                continue;
            }

            OmdbMovieDto dto = omdbClient.getByImdbId(imdbId);

            if (dto == null || !"True".equalsIgnoreCase(dto.getResponse())) {
                continue;
            }

            Movie movie = new Movie();

            // ✅ your new field
            movie.setImdbId(dto.getImdbID());

            // ✅ existing fields in your entity
            movie.setTitle(dto.getTitle());
            movie.setReleaseYear(parseYear(dto.getYear()));
            movie.setRuntimeMin(parseRuntime(dto.getRuntime())); // only if you add Runtime to DTO
            movie.setDescription(dto.getPlot());

            // ✅ optional fields (only if you added them to Movie)
            movie.setPosterUrl(fixNA(dto.getPoster()));
            movie.setImdbRating(parseRating(dto.getImdbRating()));

            movieRepository.save(movie);

            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {}
        }

        System.out.println("Movies imported.");
    }

    private Integer parseYear(String year) {
        if (year == null || year.equals("N/A")) return null;
        // handles "2012–2019" too
        String first = year.split("–")[0].trim();
        return Integer.parseInt(first);
    }

    private Integer parseRuntime(String runtime) {
        if (runtime == null || runtime.equals("N/A")) return null;
        // "142 min" -> 142
        return Integer.parseInt(runtime.replace("min", "").trim());
    }

    private BigDecimal parseRating(String rating) {
        if (rating == null || rating.equals("N/A")) return null;
        return new BigDecimal(rating);
    }

    private String fixNA(String value) {
        if (value == null || "N/A".equals(value)) return null;
        return value;
    }
}