package actionsConfigurator;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.eventsexecutor.R;

public class OverlayManager {
    public static OverlayManager instance;

    private OverlayManager(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        try {
            OverlayView overlay = (OverlayView) LayoutInflater.from(context).inflate(R.layout.overlay_layout, null);
            overlay.init(windowManager, "prova");
            new ProcessMonitor(overlay);
            Intent serviceIntent = new Intent(context, OverlayService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            Toast.makeText(context, "Servizio attivo", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Impossibile avviare il servizio", Toast.LENGTH_SHORT).show();
        }
    }

    public static OverlayManager getInstance(Context context) {
        if (instance == null) {
            instance = new OverlayManager(context);
        }
        return instance;
    }
}
