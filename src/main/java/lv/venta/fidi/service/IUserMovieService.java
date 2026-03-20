package lv.venta.fidi.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.UserMovie;

public interface IUserMovieService {

    void create(Long userId, Long movieId, WatchStatus status, LocalDate plannedDate) throws Exception;

    void update(Long userMovieId, WatchStatus status, LocalDate plannedDate) throws Exception;

    Collection<UserMovie> retrieveByUserId(Long userId) throws Exception;

    Collection<UserMovie> retrieveByMovieId(Long movieId) throws Exception;

    Optional<UserMovie> findByUserIdAndMovieId(Long userId, Long movieId) throws Exception;

    void deleteById(Long userMovieId) throws Exception;
}