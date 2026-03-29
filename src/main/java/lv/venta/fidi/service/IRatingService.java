package lv.venta.fidi.service;

import java.util.Collection;
import java.util.Optional;

import lv.venta.fidi.model.Rating;

public interface IRatingService {

    public abstract void create(Long userId, String imdbId, int ratingValue) throws Exception;

    public abstract void update(Long ratingId, int ratingValue) throws Exception;

    public abstract Collection<Rating> retrieveByUserId(Long userId) throws Exception;

    public abstract Collection<Rating> retrieveByImdbId(String imdbId) throws Exception;

    public abstract Rating retrieveById(Long ratingId) throws Exception;

    public abstract Optional<Rating> findByUserIdAndImdbId(Long userId, String imdbId) throws Exception;
}