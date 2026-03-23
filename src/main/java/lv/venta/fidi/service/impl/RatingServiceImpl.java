package lv.venta.fidi.service.impl;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.repo.IRatingRepo;
import lv.venta.fidi.service.IRatingService;

@Service
public class RatingServiceImpl implements IRatingService {

    @Autowired
    private IRatingRepo ratingRepo;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private IMovieRepo movieRepo;

    @Override
    public void create(Long userId, Long movieId, int ratingValue) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (movieId == null || movieId < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        if (ratingValue < 1 || ratingValue > 10) {
            throw new Exception("Rating value must be between 1 and 10");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

        if (ratingRepo.existsByUserAndMovie(user, movie)) {
            throw new Exception("This user has already rated this movie");
        }

        Rating rating = new Rating(user, movie, ratingValue);
        ratingRepo.save(rating);
    }

    @Override
    public void update(Long ratingId, int ratingValue) throws Exception {

        if (ratingId == null || ratingId < 0) {
            throw new Exception("Rating ID cannot be null or negative");
        }

        if (ratingValue < 1 || ratingValue > 10) {
            throw new Exception("Rating value must be between 1 and 10");
        }

        Rating rating = ratingRepo.findById(ratingId)
                .orElseThrow(() -> new Exception("Rating with ID " + ratingId + " was not found"));

        rating.setRatingValue(ratingValue);
        ratingRepo.save(rating);
    }

    @Override
    public Collection<Rating> retrieveByUserId(Long userId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        Collection<Rating> ratings = ratingRepo.findByUser(user);

        return ratings;
    }

    @Override
    public Collection<Rating> retrieveByMovieId(Long movieId) throws Exception {

        if (movieId == null || movieId < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

        Collection<Rating> ratings = ratingRepo.findByMovie(movie);

        if (ratings.isEmpty()) {
            throw new Exception("This movie has no ratings");
        }

        return ratings;
    }

    @Override
    public Optional<Rating> findByUserIdAndMovieId(Long userId, Long movieId) throws Exception {

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

        return ratingRepo.findByUserAndMovie(user, movie);
    }

    @Override
    public Rating retrieveById(Long ratingId) throws Exception {

        if (ratingId == null || ratingId < 0) {
            throw new Exception("Rating ID cannot be null or negative");
        }

        return ratingRepo.findById(ratingId)
                .orElseThrow(() -> new Exception("Rating with ID " + ratingId + " was not found"));
}
}