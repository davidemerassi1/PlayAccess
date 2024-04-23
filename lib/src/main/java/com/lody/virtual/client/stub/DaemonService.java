package com.lody.virtual.client.stub;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;


/**
 * @author Lody
 *
 */
public class DaemonService extends Service {

    private static final int NOTIFY_ID = 1001;

	public static void startup(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			context.startForegroundService(new Intent(context, DaemonService.class));
		else
			context.startService(new Intent(context, DaemonService.class));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		startup(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Notification notification = createNotification(getApplicationContext());
			startForeground(NOTIFY_ID, notification);
			startService(new Intent(this, InnerService.class));
		} else {
			startService(new Intent(this, InnerService.class));
			startForeground(NOTIFY_ID, new Notification());
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public static final class InnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				Notification notification = createNotification(getApplicationContext());
				startForeground(NOTIFY_ID, notification);
			} else {
				startForeground(NOTIFY_ID, new Notification());
			}
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
	}


	private static Notification createNotification(Context context) {
		// Creare un canale di notifica per Android 8 e versioni successive
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID", "Foreground Service", NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}

		NotificationCompat.Builder builder;
		// Costruire e restituire la notifica
		builder = new NotificationCompat.Builder(context, "YOUR_CHANNEL_ID")
				.setContentTitle("Foreground Service")
				.setContentText("Il servizio è in esecuzione in primo piano");

		return builder.build();
	}
}
