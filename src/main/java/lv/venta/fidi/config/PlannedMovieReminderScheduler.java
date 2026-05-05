package lv.venta.fidi.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lv.venta.fidi.service.PlannedMovieReminderService;

@Component
@RequiredArgsConstructor
public class PlannedMovieReminderScheduler {

    private final PlannedMovieReminderService reminderService;

    @Scheduled(cron = "${app.reminders.cron:0 0 9 * * *}")
    public void runDailyReminders() {
        reminderService.sendDayBeforeReminders();
    }
}
