package lv.venta.fidi.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import lv.venta.fidi.model.Genre;
import lv.venta.fidi.service.OmdbClient;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class GenreRepoTest {

    @Autowired
    private IGenreRepo genreRepo;

    @TestConfiguration
    static class OmdbClientMockConfig {
        @Bean
        OmdbClient omdbClient() {
            return mock(OmdbClient.class);
        }
    }

    @Test
    void testCreateGenre() {
        Genre g1 = new Genre("ThrillerRepoTest");

        Genre genreSaved = genreRepo.save(g1);

        assertEquals("ThrillerRepoTest", genreSaved.getName());
        assertNotEquals(0, genreSaved.getGenreId());
    }

    @Test
    void testFindByName() {
        Genre g1 = new Genre("DramaRepoTest");
        genreRepo.save(g1);

        Optional<Genre> result = genreRepo.findByName("DramaRepoTest");

        assertTrue(result.isPresent());
        assertEquals("DramaRepoTest", result.get().getName());
    }

    @Test
    void testUpdateGenre() {
        Genre g1 = new Genre("SciFiRepoTest");
        Genre genreSaved = genreRepo.save(g1);

        genreSaved.setName("Sci-FiRepoTestUpdated");
        Genre genreAfterEditing = genreRepo.save(genreSaved);

        assertEquals("Sci-FiRepoTestUpdated", genreAfterEditing.getName());
    }
}

