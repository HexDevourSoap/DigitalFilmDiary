package lv.venta.fidi.service;

import java.time.LocalDate;
import java.util.Collection;

import lv.venta.fidi.model.WatchEvent;

public interface IWatchEventService {

    public abstract void create(Long userId, Long movieId, LocalDate watchedAt, String notes) throws Exception;

    public abstract void update(Long watchEventId, LocalDate watchedAt, String notes) throws Exception;

    public abstract Collection<WatchEvent> retrieveByUserId(Long userId) throws Exception;

    public abstract Collection<WatchEvent> retrieveByMovieId(Long movieId) throws Exception;

    public abstract Collection<WatchEvent> retrieveByUserIdAndMovieId(Long userId, Long movieId) throws Exception;

    public abstract WatchEvent retrieveById(Long watchEventId) throws Exception;

    public abstract void deleteById(Long watchEventId) throws Exception;
}