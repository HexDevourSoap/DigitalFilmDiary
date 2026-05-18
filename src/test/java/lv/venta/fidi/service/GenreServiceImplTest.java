package lv.venta.fidi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lv.venta.fidi.model.Genre;
import lv.venta.fidi.repo.IGenreRepo;
import lv.venta.fidi.service.impl.GenreServiceImpl;

class GenreServiceImplTest {

    @Mock
    private IGenreRepo genreRepo;

    @InjectMocks
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateService() throws Exception {
        when(genreRepo.existsByName("Action")).thenReturn(false);

        genreService.create("Action");

        verify(genreRepo).save(any(Genre.class));
    }

    @Test
    void testCreateServiceExceptionWhenGenreExists() {
        when(genreRepo.existsByName("Action")).thenReturn(true);

        assertThrows(Exception.class, () -> genreService.create("Action"));
    }

    @Test
    void testFindByNameService() throws Exception {
        Genre g1 = new Genre("Comedy");
        when(genreRepo.findByName("Comedy")).thenReturn(Optional.of(g1));

        Optional<Genre> genreFromService = genreService.findByName("Comedy");

        assertEquals(true, genreFromService.isPresent());
        assertEquals("Comedy", genreFromService.get().getName());
    }
}

