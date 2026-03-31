package lv.venta.fidi.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.UserMovie;

public interface IUserMovieService {

    public abstract void create(Long userId, String imdbId, WatchStatus status, LocalDate plannedDate, String notes) throws Exception;

    public abstract void update(Long userMovieId, WatchStatus status, LocalDate plannedDate, String notes) throws Exception;

    public abstract Collection<UserMovie> retrieveByUserId(Long userId) throws Exception;

    public abstract Collection<UserMovie> retrieveByImdbId(String imdbId) throws Exception;

    public abstract UserMovie retrieveById(Long userMovieId) throws Exception;

    public abstract Optional<UserMovie> findByUserIdAndImdbId(Long userId, String imdbId) throws Exception;

    public abstract void deleteById(Long userMovieId) throws Exception;
}