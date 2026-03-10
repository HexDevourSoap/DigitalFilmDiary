package lv.venta.fidi.service;

import java.util.Collection;
import java.util.Optional;


import lv.venta.fidi.model.Rating;

public interface IRatingService {

    public abstract void create(Long userId, Long movieId, int ratingValue) throws Exception;

    public abstract void update(Long ratingId, int ratingValue) throws Exception;

    public abstract Collection<Rating> retrieveByUserId(Long userId) throws Exception;

    public abstract Collection<Rating> retrieveByMovieId(Long movieId) throws Exception;

    public abstract Optional<Rating> findByUserIdAndMovieId(Long userId, Long movieId) throws Exception;

}