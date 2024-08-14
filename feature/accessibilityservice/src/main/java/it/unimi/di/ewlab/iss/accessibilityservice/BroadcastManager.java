package it.unimi.di.ewlab.iss.accessibilityservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import it.unimi.di.ewlab.iss.actionsrecognizer.ActionListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.model.actions.OtherModuleAction;

public class BroadcastManager extends BroadcastReceiver {
    private EventExecutor executor;

    public BroadcastManager(Context context, EventExecutor executor) {
        this.executor = executor;
        IntentFilter filter = new IntentFilter();
        filter.addAction("it.unimi.di.ewlab.iss.ACTION_START");
        filter.addAction("it.unimi.di.ewlab.iss.ACTION_END");
        filter.addAction("it.unimi.di.ewlab.iss.ACTION_2D_MOVEMENT");
        filter.addAction("it.unimi.di.ewlab.iss.ACTION_REPLY");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        } else
            context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "it.unimi.di.ewlab.iss.ACTION_START":
                String actionName = intent.getStringExtra("action");
                Action action = new OtherModuleAction(actionName, false);
                executor.onActionStarts(action);
                break;
            case "it.unimi.di.ewlab.iss.ACTION_END":
                actionName = intent.getStringExtra("action");
                action = new OtherModuleAction(actionName, false);
                executor.onActionEnds(action);
                break;
            case "it.unimi.di.ewlab.iss.ACTION_REPLY":
                //ogni azione nella forma "nome;is2d", es. "bocca aperta;false"
                String[] actionsArray = intent.getStringArrayExtra("actions");
                List<Action> actionList = new ArrayList<>();
                for (String s : actionsArray) {
                    String[] actionData = s.split(";");
                    action = new OtherModuleAction(actionData[0], Boolean.parseBoolean(actionData[1]));
                    actionList.add(action);
                }
                MainModel.getInstance().setOtherModulesActions(actionList);
                break;
            case "it.unimi.di.ewlab.iss.ACTION_2D_MOVEMENT":
                actionName = intent.getStringExtra("action");
                action = new OtherModuleAction(actionName, true);
                float x = intent.getFloatExtra("x", 0);
                float y = intent.getFloatExtra("y", 0);
                executor.on2dMovement(action, x, y);
                Log.d("ActionsBroadcastReceiver", "2D Movement: x=" + x + ", y=" + y);
                break;
        }
    }
}
