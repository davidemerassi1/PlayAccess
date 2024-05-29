package actionsConfigurator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KeyBroadcastReceiver extends BroadcastReceiver {
    private KeyEventListener keyEventListener;

    public KeyBroadcastReceiver(KeyEventListener keyEventListener) {
        this.keyEventListener = keyEventListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
