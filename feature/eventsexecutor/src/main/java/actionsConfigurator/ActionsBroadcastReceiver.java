package actionsConfigurator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class ActionsBroadcastReceiver extends BroadcastReceiver {
    private OverlayView overlay;
    private MutableLiveData<List<Action>> actionsLiveData;

    public ActionsBroadcastReceiver(OverlayView overlay, Context context) {
        this.overlay = overlay;

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.accessibilityservice.ACTION_START");
        filter.addAction("com.example.accessibilityservice.ACTION_END");
        filter.addAction("com.example.accessibilityservice.ACTION_REPLY");
        filter.addAction("com.example.accessibilityservice.PACKAGE_CHANGED");
        filter.addAction("com.example.accessibilityservice.ACTION_2D_MOVEMENT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        } else
            context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "com.example.accessibilityservice.ACTION_START":
                Action action = (Action) intent.getSerializableExtra("action");
                overlay.onActionStarts(action);
                break;
            case "com.example.accessibilityservice.ACTION_END":
                Log.d("ActionsBroadcastReceiver", "Action end");
                Action actionEnd = (Action) intent.getSerializableExtra("action");
                overlay.onActionEnds(actionEnd);
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
            case "com.example.accessibilityservice.PACKAGE_CHANGED":
                MainModel.getInstance().setActivePackage(intent.getStringExtra("packageName"));
                break;
            case "com.example.accessibilityservice.ACTION_2D_MOVEMENT":
                float x = intent.getFloatExtra("x", 0);
                float y = intent.getFloatExtra("y", 0);
                overlay.on2dMovement(x, y);
                Log.d("ActionsBroadcastReceiver", "2D Movement: x=" + x + ", y=" + y);
                break;
        }
    }

    public void requestActions(MutableLiveData<List<Action>> actionsLiveData) {
        Intent intent = new Intent("com.example.accessibilityservice.ACTION_REQUEST");
        overlay.getContext().sendBroadcast(intent);
        this.actionsLiveData = actionsLiveData;
    }
}
