package lv.venta.fidi.service;

import java.time.LocalDate;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lv.venta.fidi.enums.WatchStatus;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.UserMovie;
import lv.venta.fidi.repo.IMovieRepo;
import lv.venta.fidi.repo.IUserMovieRepo;

@Service
@RequiredArgsConstructor
public class PlannedMovieReminderService {

    private static final Logger log = LoggerFactory.getLogger(PlannedMovieReminderService.class);

    private final IUserMovieRepo userMovieRepo;
    private final IMovieRepo movieRepo;
    private final JavaMailSender mailSender;

    @Value("${app.reminders.enabled:true}")
    private boolean remindersEnabled;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${app.reminders.from:}")
    private String fromEmail;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public void sendDayBeforeReminders() {
        if (!remindersEnabled) {
            return;
        }
        if (mailHost == null || mailHost.isBlank()) {
            log.info("Reminder emails skipped: spring.mail.host is not configured.");
            return;
        }
        if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
            log.info("Reminder emails skipped: spring.mail.username / spring.mail.password not set.");
            return;
        }

        String effectiveFrom = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail : mailUsername;
        if (effectiveFrom == null || effectiveFrom.isBlank()) {
            log.info("Reminder emails skipped: no From address (set app.reminders.from or spring.mail.username).");
            return;
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Collection<UserMovie> plannedTomorrow = userMovieRepo.findByStatusAndPlannedDate(WatchStatus.PLAN_TO_WATCH, tomorrow);
        log.info("Planned-movie reminders (D-1): found {} row(s) for plannedDate={}", plannedTomorrow.size(), tomorrow);

        for (UserMovie item : plannedTomorrow) {
            if (item.getUser() == null || item.getUser().getEmail() == null || item.getUser().getEmail().isBlank()) {
                continue;
            }

            String movieTitle = resolveMovieTitle(item.getImdbId());
            sendReminder(effectiveFrom, item.getUser().getEmail(), movieTitle, tomorrow);
        }
    }

    private String resolveMovieTitle(String imdbId) {
        if (imdbId == null || imdbId.isBlank()) {
            return "filma";
        }
        return movieRepo.findByImdbId(imdbId)
                .map(Movie::getTitle)
                .filter(t -> t != null && !t.isBlank())
                .orElse(imdbId);
    }

    private void sendReminder(String fromAddress, String toEmail, String movieTitle, LocalDate plannedDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Atgādinājums: plānotā filma rīt");
        message.setText("Sveiki!\n\n"
                + "Atgādinājums: rīt plānojat noskatīties filmu \"" + movieTitle + "\".\n"
                + "Plānotais datums: " + plannedDate + ".\n\n"
                + "Labu skatīšanos!\n"
                + "Filmu dienasgrāmata");
        try {
            mailSender.send(message);
            log.info("Reminder email sent to {} (movie: {})", toEmail, movieTitle);
        } catch (Exception ex) {
            log.warn("Reminder email failed for {} (movie: {}): {}", toEmail, movieTitle, ex.getMessage());
        }
    }
}
