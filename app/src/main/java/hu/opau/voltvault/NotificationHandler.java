package hu.opau.voltvault;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.app.NotificationManager.IMPORTANCE_MAX;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private NotificationManager manager;
    private Context c;

    public NotificationHandler(Context c) {
        this.manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        this.c = c;
        createChannel();
    }

    private void createChannel() {
        NotificationChannel channel = new NotificationChannel("basket_reminder_channel", "Kosáremlékeztető", IMPORTANCE_HIGH);
        channel.setDescription("Emlékeztet, ha a kosaradban maradt termékeket elfelejtetted volna megvenni.");
        manager.createNotificationChannel(channel);
    }

    public void send(String channel, String title, String message) {
        Intent i = new Intent(c, MainActivity.class);
        i.putExtra("screen", 2);
        PendingIntent pi = PendingIntent.getActivity(c, 0, i, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, channel)
                .setContentTitle(title)
                .setColor(c.getColor(R.color.brand_fg))
                .setContentText(message)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.monochrome_logo);
        manager.notify(0, builder.build());
    }

    public void cancel() {
        this.manager.cancel(0);
    }
}
