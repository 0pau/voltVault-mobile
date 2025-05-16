package hu.opau.voltvault;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import hu.opau.voltvault.controller.BasketController;

public class BasketReminderJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        new NotificationHandler(getApplicationContext())
                .send("basket_reminder_channel", "Megfeledkeztél a kosaradról", "A kosaradban " + BasketController.getInstance().getBasketItems().size() + " termék maradt.\nKoppints az értesítésre a megtekintéshez.");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
