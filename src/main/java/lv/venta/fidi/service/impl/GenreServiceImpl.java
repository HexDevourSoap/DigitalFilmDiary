package lv.venta.fidi.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.model.Genre;
import lv.venta.fidi.repo.IGenreRepo;
import lv.venta.fidi.service.IGenreService;

@Service
public class GenreServiceImpl implements IGenreService {

    @Autowired
    private IGenreRepo genreRepo;

    @Override
    public void create(String name) throws Exception {

        if (name == null || name.isBlank()) {
            throw new Exception("Genre name cannot be empty");
        }

        if (genreRepo.existsByName(name)) {
            throw new Exception("Genre with name " + name + " already exists");
        }

        Genre genre = new Genre(name);
        genreRepo.save(genre);
    }

    @Override
    public void update(Long id, String name) throws Exception {

        if (id == null || id < 0) {
            throw new Exception("ID cannot be null or negative");
        }

        if (name == null || name.isBlank()) {
            throw new Exception("Genre name cannot be empty");
        }

        Genre genre = genreRepo.findById(id)
                .orElseThrow(() -> new Exception("Genre with ID " + id + " was not found"));

        Optional<Genre> existingGenre = genreRepo.findByName(name);

        if (existingGenre.isPresent() && existingGenre.get().getGenreId() != id) {
            throw new Exception("Another genre with name " + name + " already exists");
        }

        genre.setName(name);

        genreRepo.save(genre);
    }

    @Override
    public Optional<Genre> findByName(String name) throws Exception {

        if (name == null || name.isBlank()) {
            throw new Exception("Genre name cannot be empty");
        }

        return genreRepo.findByName(name);
    }

}