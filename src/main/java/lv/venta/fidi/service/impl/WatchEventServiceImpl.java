package lv.venta.fidi.service.impl;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.WatchEvent;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.repo.IWatchEventRepo;
import lv.venta.fidi.service.IWatchEventService;

@Service
public class WatchEventServiceImpl implements IWatchEventService {

    @Autowired
    private IWatchEventRepo watchEventRepo;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Autowired
    private IMovieRepo movieRepo;

    @Override
    public void create(Long userId, Long movieId, LocalDate watchedAt, String notes) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (movieId == null || movieId < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        if (watchedAt == null) {
            throw new Exception("Watched date cannot be null");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

        WatchEvent watchEvent = new WatchEvent(user, movie, watchedAt, notes);

        watchEventRepo.save(watchEvent);
    }

    @Override
    public void update(Long watchEventId, LocalDate watchedAt, String notes) throws Exception {

        if (watchEventId == null || watchEventId < 0) {
            throw new Exception("WatchEvent ID cannot be null or negative");
        }

        WatchEvent watchEvent = watchEventRepo.findById(watchEventId)
                .orElseThrow(() -> new Exception("WatchEvent with ID " + watchEventId + " was not found"));

        if (watchedAt != null) {
            watchEvent.setWatchedAt(watchedAt);
        }

        watchEvent.setNotes(notes);

        watchEventRepo.save(watchEvent);
    }

    @Override
    public Collection<WatchEvent> retrieveByUserId(Long userId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        Collection<WatchEvent> events = watchEventRepo.findByUser(user);

        if (events.isEmpty()) {
            throw new Exception("This user has no watch events");
        }

        return events;
    }

    @Override
    public Collection<WatchEvent> retrieveByMovieId(Long movieId) throws Exception {

        if (movieId == null || movieId < 0) {
            throw new Exception("Movie ID cannot be null or negative");
        }

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new Exception("Movie with ID " + movieId + " was not found"));

        Collection<WatchEvent> events = watchEventRepo.findByMovie(movie);

        if (events.isEmpty()) {
            throw new Exception("This movie has no watch events");
        }

        return events;
    }

    @Override
    public Collection<WatchEvent> retrieveByUserIdAndMovieId(Long userId, Long movieId) throws Exception {

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

        return watchEventRepo.findByUserAndMovie(user, movie);
    }
}