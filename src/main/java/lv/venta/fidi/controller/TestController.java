package lv.venta.fidi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.repo.IMovieRepo;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final IMovieRepo movieRepo;

    @GetMapping("/test/movies")
    public List<Movie> getMovies() {
        return movieRepo.findAll();
    }
}