package lv.venta.fidi.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.UserMovie;

public interface IUserMovieService {

    void create(Long userId, String imdbId, WatchStatus status, LocalDate plannedDate) throws Exception;

    void update(Long userMovieId, WatchStatus status, LocalDate plannedDate) throws Exception;

    Collection<UserMovie> retrieveByUserId(Long userId) throws Exception;

    Collection<UserMovie> retrieveByImdbId(String imdbId) throws Exception;

    UserMovie retrieveById(Long userMovieId) throws Exception;

    Optional<UserMovie> findByUserIdAndImdbId(Long userId, String imdbId) throws Exception;

    void deleteById(Long userMovieId) throws Exception;
}