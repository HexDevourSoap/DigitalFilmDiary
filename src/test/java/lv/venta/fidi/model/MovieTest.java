package lv.venta.fidi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class MovieTest {

    private static Movie movieGood;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        movieGood = new Movie("tt0133093", "The Matrix", 1999, 136, "Sci-fi action movie.");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testMovieGoodValues() {
        assertEquals("tt0133093", movieGood.getImdbId());
        assertEquals("The Matrix", movieGood.getTitle());
        assertEquals(1999, movieGood.getReleaseYear());
        assertEquals(136, movieGood.getRuntimeMin());
        assertEquals("Sci-fi action movie.", movieGood.getDescription());
    }

    @Test
    void testMovieValidationBlankImdbId() {
        Movie invalid = new Movie("", "Good title", 2000, 120, "Desc");

        Set<ConstraintViolation<Movie>> result = validator.validate(invalid);

        assertEquals(1, result.size());
        assertTrue(result.iterator().next().getMessage().toLowerCase().contains("blank"));
    }

    @Test
    void testMovieValidationBlankTitle() {
        Movie invalid = new Movie("tt1234567", "", 2000, 120, "Desc");

        Set<ConstraintViolation<Movie>> result = validator.validate(invalid);

        assertEquals(1, result.size());
        assertTrue(result.iterator().next().getMessage().toLowerCase().contains("blank"));
    }

    @Test
    void testMovieValidationReleaseYearTooLow() {
        Movie invalid = new Movie("tt1234567", "Good title", 1800, 120, "Desc");

        Set<ConstraintViolation<Movie>> result = validator.validate(invalid);

        assertEquals(1, result.size());
        assertTrue(result.iterator().next().getMessage().contains("1888"));
    }
}

