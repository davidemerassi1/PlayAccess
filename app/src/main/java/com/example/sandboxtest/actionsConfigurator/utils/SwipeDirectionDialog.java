package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Action;

public class SwipeDirectionDialog extends LinearLayout {
    private ListView listView;
    private AdapterView.OnItemClickListener listener;

    public SwipeDirectionDialog(Context context) {
        super(context);
    }

    public SwipeDirectionDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeDirectionDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(AdapterView.OnItemClickListener listener) {
        Action[] directions = {Action.SWIPE_UP, Action.SWIPE_DOWN, Action.SWIPE_LEFT, Action.SWIPE_RIGHT};
        listView = findViewById(R.id.swipeDirectionListView);
        ArrayAdapter<Action> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, directions);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
    }
}
