package lv.venta.fidi.service.impl;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Rating;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IRatingRepo;
import lv.venta.fidi.service.IRatingService;

@Service
public class RatingServiceImpl implements IRatingService {

    @Autowired
    private IRatingRepo ratingRepo;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Override
    public void create(Long userId, String imdbId, int ratingValue) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        if (ratingValue < 1 || ratingValue > 10) {
            throw new Exception("Rating value must be between 1 and 10");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        if (ratingRepo.existsByUserAndImdbId(user, imdbId)) {
            throw new Exception("This user has already rated this movie");
        }

        Rating rating = new Rating(user, imdbId, ratingValue);
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

        return ratingRepo.findByUser(user);
    }

    @Override
    public Collection<Rating> retrieveByImdbId(String imdbId) throws Exception {

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        return ratingRepo.findByImdbId(imdbId);
    }

    @Override
    public Optional<Rating> findByUserIdAndImdbId(Long userId, String imdbId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        return ratingRepo.findByUserAndImdbId(user, imdbId);
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