package lv.venta.fidi.service;

import java.util.Optional;

import lv.venta.fidi.model.Genre;

public interface IGenreService{

    public abstract void create(String name) throws Exception;

    public abstract void update(Long id, String name) throws Exception;

    public abstract Optional<Genre> findByName(String name) throws Exception;

}