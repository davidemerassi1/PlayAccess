package com.example.accessibilityservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.actionsrecognizer.facialexpressionactionsrecognizer.ActionListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class BroadcastManager implements ActionListener {
    private Context context;

    public BroadcastManager(Context context) {
        this.context = context;
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

    public enum ActionType {
        ACTION_START,
        ACTION_END
    }
}
