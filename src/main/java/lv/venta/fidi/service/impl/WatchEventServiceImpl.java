package lv.venta.fidi.service.impl;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.WatchEvent;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.repo.IWatchEventRepo;
import lv.venta.fidi.service.IWatchEventService;

@Service
public class WatchEventServiceImpl implements IWatchEventService {

    @Autowired
    private IWatchEventRepo watchEventRepo;

    @Autowired
    private IAppUserRepo appUserRepo;

    @Override
    public void create(Long userId, String imdbId, LocalDate watchedAt, String notes) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        if (watchedAt == null) {
            throw new Exception("Watched date cannot be null");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        WatchEvent watchEvent = new WatchEvent(user, imdbId, watchedAt, notes);

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

        return watchEventRepo.findByUser(user);
    }

    @Override
    public Collection<WatchEvent> retrieveByImdbId(String imdbId) throws Exception {

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        return watchEventRepo.findByImdbId(imdbId);
    }

    @Override
    public Collection<WatchEvent> retrieveByUserIdAndImdbId(Long userId, String imdbId) throws Exception {

        if (userId == null || userId < 0) {
            throw new Exception("User ID cannot be null or negative");
        }

        if (imdbId == null || imdbId.isBlank()) {
            throw new Exception("IMDb ID cannot be empty");
        }

        AppUser user = appUserRepo.findById(userId)
                .orElseThrow(() -> new Exception("User with ID " + userId + " was not found"));

        return watchEventRepo.findByUserAndImdbId(user, imdbId);
    }

    @Override
    public WatchEvent retrieveById(Long watchEventId) throws Exception {

        if (watchEventId == null || watchEventId < 0) {
            throw new Exception("WatchEvent ID cannot be null or negative");
        }

        return watchEventRepo.findById(watchEventId)
                .orElseThrow(() -> new Exception("WatchEvent with ID " + watchEventId + " was not found"));
    }

    @Override
    public void deleteById(Long watchEventId) throws Exception {

        if (watchEventId == null || watchEventId < 0) {
            throw new Exception("WatchEvent ID cannot be null or negative");
        }

        WatchEvent watchEvent = watchEventRepo.findById(watchEventId)
                .orElseThrow(() -> new Exception("WatchEvent with ID " + watchEventId + " was not found"));

        watchEventRepo.delete(watchEvent);
    }
}