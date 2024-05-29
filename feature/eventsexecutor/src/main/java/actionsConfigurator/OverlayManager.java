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

import androidx.lifecycle.MutableLiveData;

import com.example.eventsexecutor.R;

import java.util.ArrayList;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction;

public class OverlayManager extends BroadcastReceiver {
    public static OverlayManager instance;
    private OverlayView overlay;
    private MutableLiveData<List<Action>> actionsLiveData;

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
        filter.addAction("com.example.accessibilityservice.ACTION_REPLY");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        } else
            context.registerReceiver(this, filter);
    }

    public static OverlayManager getInstance(Context context) {
        if (instance == null) {
            instance = new OverlayManager(context);
        } else
            Toast.makeText(context, "Servizio già attivo: avvia il tuo gioco preferito!", Toast.LENGTH_SHORT).show();
        return instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "com.example.accessibilityservice.ACTION_START":
                Action action = (Action) intent.getSerializableExtra("action");
                overlay.onActionStarts(action);
                break;
            case "com.example.accessibilityservice.ACTION_REPLY":
                if (actionsLiveData != null) {
                    Object[] actionsArray = (Object[]) intent.getSerializableExtra("actions");
                    ArrayList<Action> actionList = new ArrayList<>();
                    for (Object a : actionsArray) {
                        actionList.add((Action) a);
                    }
                    actionList.add(OverlayView.FACE_MOVEMENT_ACTION);
                    actionsLiveData.setValue(actionList);
                }
                actionsLiveData = null;
                break;
        }
    }

    public void requestActions(MutableLiveData<List<Action>> actionsLiveData) {
        Intent intent = new Intent("com.example.accessibilityservice.ACTION_REQUEST");
        overlay.getContext().sendBroadcast(intent);
        this.actionsLiveData = actionsLiveData;
    }
}
