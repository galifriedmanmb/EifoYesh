package com.gali.apps.eifoyesh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * notifies when changes in power connectivity happen
 */

public class PlugInControlReceiver extends BroadcastReceiver {
    public void onReceive(Context context , Intent intent) {
        String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            Toast.makeText(context, context.getResources().getString(R.string.powerConnected), Toast.LENGTH_SHORT).show();
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Toast.makeText(context, context.getResources().getString(R.string.powerDisconnected), Toast.LENGTH_SHORT).show();
        }
    }
}
