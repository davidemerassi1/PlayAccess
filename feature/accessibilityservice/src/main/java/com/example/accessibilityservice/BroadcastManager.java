package com.example.accessibilityservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.example.actionsrecognizer.facialexpressionactionsrecognizer.ActionListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class BroadcastManager extends BroadcastReceiver implements ActionListener {
    private Context context;
    private CameraLifecycle cameraLifecycle;

    public BroadcastManager(Context context, CameraLifecycle cameraLifecycle) {
        this.context = context;
        this.cameraLifecycle = cameraLifecycle;
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.accessibilityservice.ACTION_REQUEST");
        filter.addAction("com.example.accessibilityservice.NEED_CAMERA");
        filter.addAction("com.example.accessibilityservice.NO_CAMERA");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        } else
            context.registerReceiver(this, filter);
    }

    @Override
    public void onActionStarts(@NonNull Action action) {
        Intent intent = new Intent("com.example.accessibilityservice.ACTION_START");
        intent.putExtra("action", action.lighten());
        context.sendBroadcast(intent);
    }

    @Override
    public void onActionEnds(@NonNull Action action) {
        Intent intent = new Intent("com.example.accessibilityservice.ACTION_END");
        intent.putExtra("action", action.lighten());
        context.sendBroadcast(intent);
    }

    @Override
    public void on2dMovement(float x, float y) {
        Intent intent = new Intent("com.example.accessibilityservice.ACTION_2D_MOVEMENT");
        intent.putExtra("x", x);
        intent.putExtra("y", y);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "com.example.accessibilityservice.ACTION_REQUEST":
                List<Action> actions = MainModel.getInstance().getActions();
                List<Action> lightenedActions = new ArrayList<>();
                for (Action action : actions) {
                    if (!action.getName().equals("Espressione neutrale"))
                        lightenedActions.add(action.lighten());
                }
                Intent intent1 = new Intent("com.example.accessibilityservice.ACTION_REPLY");
                intent1.putExtra("actions", lightenedActions.toArray());
                context.sendBroadcast(intent1);
                break;
            case "com.example.accessibilityservice.NEED_CAMERA":
                cameraLifecycle.resume();
                break;
            case "com.example.accessibilityservice.NO_CAMERA":
                cameraLifecycle.pause();
                break;
        }
    }
}
