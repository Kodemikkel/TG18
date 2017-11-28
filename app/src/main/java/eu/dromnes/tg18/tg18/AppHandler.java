package eu.dromnes.tg18.tg18;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

class AppHandler extends Handler {
    private final WeakReference<MainActivity> activity;

    AppHandler(MainActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        MainActivity activity = this.activity.get();
        if(activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.toolBar);
            switch(msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    // TODO: ADD CODE FOR DEALING WITH CHANGES IN CONNECTION STATE
                    switch(msg.arg1) {
                        case BluetoothService.STATE_NONE:
                            Log.d("DEBUG", "STATE_NONE");
                            if(toolbar != null) {
                                toolbar.setSubtitle(R.string.title_btNotConnected);
                            }
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.d("DEBUG", "STATE_CONNECTING");
                            if(toolbar != null) {
                                toolbar.setSubtitle(R.string.title_btConnecting);
                            }
                            break;
                        case BluetoothService.STATE_CONNECTED:
                            Log.d("DEBUG", "STATE_CONNECTED");
                            if(toolbar != null) {
                                toolbar.setSubtitle(R.string.title_btConnected);
                            }
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    // TODO: ADD CODE FOR DEALING WITH DATA RETURNED FROM THE CONTROLLER
                    byte[] readBuffer = (byte[]) msg.obj;
                    String readMessage = new String(readBuffer, 0, msg.arg1);
                    Log.d("DATA_RECEIVED", readMessage);
                    break;
                case Constants.MESSAGE_TOAST:
                    if (activity != null) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}