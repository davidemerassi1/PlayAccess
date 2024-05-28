package actionsConfigurator;

import static android.content.Context.WINDOW_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.eventsexecutor.R;

import java.util.Set;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction;
import it.unimi.di.ewlab.iss.common.model.actionsmodels.ButtonActionsModel;

public class OverlayManager extends BroadcastReceiver {
    public static OverlayManager instance;
    private OverlayView overlay;

    private OverlayManager(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        try {
            overlay = (OverlayView) LayoutInflater.from(context).inflate(R.layout.overlay_layout, null);
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

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.accessibilityservice.ACTION_START");
        filter.addAction("com.example.accessibilityservice.ACTION_END");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        } else
            context.registerReceiver(this, filter);
    }

    public static OverlayManager getInstance(Context context) {
        if (instance == null) {
            instance = new OverlayManager(context);
        }
        return instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int keyCode = intent.getIntExtra("key_code", 0);
        int source = intent.getIntExtra("source", 0);
        switch (intent.getAction()) {
            case "com.example.accessibilityservice.ACTION_START":
                if (keyCode != 0) {
                    ButtonAction ba = new ButtonAction(0, /*KeyEvent.keyCodeToString(keyCode)*/ "centro", String.valueOf(source), String.valueOf(keyCode));
                    Log.d("OverlayManager", "onReceive: " + intent.getAction() + " " + keyCode);
                    overlay.onActionStarts(ba);

                    //da rimuovere
                    MainModel.getInstance().setTempButtonAction(ba);
                    MainModel.getInstance().setTempButtonAction(null);
                }
                break;
            default:
                Log.d("OverlayManager", "onReceive: " + intent.getAction() + " " + keyCode);
        }

    }
}
