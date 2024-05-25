package actionsConfigurator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.eventsexecutor.R;

import it.unimi.di.ewlab.iss.common.model.MainModel;

public class OverlayService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
    }

    private void showNotification() {
        // Creazione del canale per le notifiche (necessario per Android 8.0 e versioni successive)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Creazione della notifica
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("PlayAccess è in esecuzione")
                .setContentText("Apri " + MainModel.getInstance().getSandboxName() + " e avvia il tuo gioco preferito!")
                .setSmallIcon(R.drawable.playaccess_logo_notification)
                .build();

        // Associazione della notifica al servizio in primo piano (foreground service)
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
