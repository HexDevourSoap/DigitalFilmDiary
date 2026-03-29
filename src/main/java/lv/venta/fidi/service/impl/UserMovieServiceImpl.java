package lv.venta.fidi.service.impl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IUserMovieRepo;
import lv.venta.fidi.service.IUserMovieService;

@Service
public class UserMovieServiceImpl implements IUserMovieService {

    @Autowired
    private IUserMovieRepo userMovieRepo;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Override
    public void create(Long userId, String imdbId, WatchStatus status, LocalDate plannedDate) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        if (status == null) {
            throw new Exception("Watch status cannot be null");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        if (userMovieRepo.existsByUserAndImdbId(user, imdbId)) {
            throw new Exception("This user already has this movie in diary");
        }

        UserMovie userMovie = new UserMovie(user, imdbId, status, plannedDate);
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

        return userMovieRepo.findByUser(user);
    }

    @Override
    public Collection<UserMovie> retrieveByImdbId(String imdbId) throws Exception {

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        return userMovieRepo.findByImdbId(imdbId);
    }

    @Override
    public Optional<UserMovie> findByUserIdAndImdbId(Long userId, String imdbId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        return userMovieRepo.findByUserAndImdbId(user, imdbId);
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