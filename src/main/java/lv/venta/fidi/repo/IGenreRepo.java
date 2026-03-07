package lv.venta.fidi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.Genre;

public interface IGenreRepo extends JpaRepository<Genre, Long> {

    public abstract Optional<Genre> findByName(String name);

    public abstract boolean existsByName(String name);

}