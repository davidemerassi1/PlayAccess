package com.example.accessibilityservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.example.actionsrecognizer.facialexpressionactionsrecognizer.ActionListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class BroadcastManager extends BroadcastReceiver implements ActionListener {
    private Context context;

    public BroadcastManager(Context context) {
        this.context = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.accessibilityservice.ACTION_REQUEST");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        } else
            context.registerReceiver(this, filter);
    }

    @Override
    public void onActionStarts(@NonNull Action action) {
        Log.d("BroadcastManager", "onActionStarts: " + action.getName());
    }

    @Override
    public void onActionEnds(@NonNull Action action) {
        Log.d("BroadcastManager", "onActionEnds: " + action.getName());
    }

    @Override
    public void on2dMovement(float x, float y) {
        Log.d("BroadcastManager", "on2dMovement: " + x + " " + y);
    }

    public void sendKeyEvent(ActionType actionType, int keyCode, int source) {
        Intent intent;
        switch (actionType) {
            case ACTION_START:
                intent = new Intent("com.example.accessibilityservice.ACTION_START");
                break;
            case ACTION_END:
                intent = new Intent("com.example.accessibilityservice.ACTION_END");
                break;
            default:
                return;
        }
        intent.putExtra("key_code", keyCode);
        intent.putExtra("source", source);
        context.sendBroadcast(intent);
    }

    public void sendAction(Action action, ActionType actionType) {
        Intent intent;
        switch (actionType) {
            case ACTION_START:
                intent = new Intent("com.example.accessibilityservice.ACTION_START");
                break;
            case ACTION_END:
                intent = new Intent("com.example.accessibilityservice.ACTION_END");
                break;
            default:
                return;
        }
        intent.putExtra("action", action);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.accessibilityservice.ACTION_REQUEST")) {
            List<Action> actions = MainModel.getInstance().getActions();
            Intent intent1 = new Intent("com.example.accessibilityservice.ACTION_REPLY");
            intent1.putExtra("actions", actions.toArray());
            context.sendBroadcast(intent1);
        }
    }

    public enum ActionType {
        ACTION_START,
        ACTION_END
    }
}
