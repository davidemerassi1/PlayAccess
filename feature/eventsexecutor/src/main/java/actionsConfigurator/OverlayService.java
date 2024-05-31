package actionsConfigurator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.eventsexecutor.R;

import java.util.List;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class OverlayService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private OverlayView overlay;
    private static OverlayService instance;
    private ActionsBroadcastReceiver actionsBroadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlay = (OverlayView) LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
        overlay.init(windowManager);
        actionsBroadcastReceiver = new ActionsBroadcastReceiver(overlay, this);
        new ProcessMonitor(overlay);
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static OverlayService getInstance() {
        return instance;
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

    public void requestActions(MutableLiveData<List<Action>> actionsLiveData) {
        actionsBroadcastReceiver.requestActions(actionsLiveData);
    }

    @Override
    public void onDestroy() {
        sendBroadcast(new Intent("com.example.accessibilityservice.NO_CAMERA"));
        super.onDestroy();
    }
}
