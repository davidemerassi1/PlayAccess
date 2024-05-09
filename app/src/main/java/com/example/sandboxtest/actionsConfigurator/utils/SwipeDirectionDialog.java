package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Event;

public class SwipeDirectionDialog extends FrameLayout {
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
        Event[] directions = {Event.SWIPE_UP, Event.SWIPE_DOWN, Event.SWIPE_LEFT, Event.SWIPE_RIGHT};
        listView = findViewById(R.id.swipeDirectionListView);
        ArrayAdapter<Event> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, directions);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
    }
}
