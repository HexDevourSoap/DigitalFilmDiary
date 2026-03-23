package lv.venta.fidi.service.impl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.repo.IUserMovieRepo;
import lv.venta.fidi.service.IUserMovieService;

@Service
public class UserMovieServiceImpl implements IUserMovieService {

    @Autowired
    private IUserMovieRepo userMovieRepo;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private IMovieRepo movieRepo;

    @Override
    public void create(Long userId, Long movieId, WatchStatus status, LocalDate plannedDate) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (movieId == null || movieId < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        if (status == null) {
            throw new Exception("Watch status cannot be null");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

        if (userMovieRepo.existsByUserAndMovie(user, movie)) {
            throw new Exception("This user already has this movie in diary");
        }

        UserMovie userMovie = new UserMovie(user, movie, status, plannedDate);
        userMovieRepo.save(userMovie);
    }

    @Override
    public void update(Long userMovieId, WatchStatus status, LocalDate plannedDate) throws Exception {

        if (userMovieId == null || userMovieId < 0) {
            throw new Exception("UserMovie ID cannot be null or negative");
        }

        if (status == null) {
            throw new Exception("Watch status cannot be null");
        }

        UserMovie userMovie = userMovieRepo.findById(userMovieId)
                .orElseThrow(() -> new Exception("UserMovie with ID " + userMovieId + " was not found"));

        userMovie.setStatus(status);
        userMovie.setPlannedDate(plannedDate);

        userMovieRepo.save(userMovie);
    }

    @Override
    public Collection<UserMovie> retrieveByUserId(Long userId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        Collection<UserMovie> userMovies = userMovieRepo.findByUser(user);

        if (userMovies.isEmpty()) {
            throw new Exception("This user has no diary entries");
        }

        return userMovies;
    }

    @Override
    public Collection<UserMovie> retrieveByMovieId(Long movieId) throws Exception {

        if (movieId == null || movieId < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

        Collection<UserMovie> userMovies = userMovieRepo.findByMovie(movie);

        if (userMovies.isEmpty()) {
            throw new Exception("This movie is not in any user's diary");
        }

        return userMovies;
    }

    @Override
    public Optional<UserMovie> findByUserIdAndMovieId(Long userId, Long movieId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (movieId == null || movieId < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

        return userMovieRepo.findByUserAndMovie(user, movie);
    }

    @Override
    public UserMovie retrieveById(Long userMovieId) throws Exception {

        if (userMovieId == null || userMovieId < 0) {
            throw new Exception("UserMovie ID cannot be null or negative");
        }

        return userMovieRepo.findById(userMovieId)
                .orElseThrow(() -> new Exception("UserMovie with ID " + userMovieId + " was not found"));
    }

    @Override
    public void deleteById(Long userMovieId) throws Exception {

        if (userMovieId == null || userMovieId < 0) {
            throw new Exception("UserMovie ID cannot be null or negative");
        }

        UserMovie userMovie = userMovieRepo.findById(userMovieId)
                .orElseThrow(() -> new Exception("UserMovie with ID " + userMovieId + " was not found"));

        userMovieRepo.delete(userMovie);
    }
}