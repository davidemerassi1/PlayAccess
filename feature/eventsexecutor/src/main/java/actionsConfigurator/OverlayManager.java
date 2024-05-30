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

public class OverlayManager {
    public static OverlayManager instance;

    public OverlayManager(Context context) {
        try {
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
