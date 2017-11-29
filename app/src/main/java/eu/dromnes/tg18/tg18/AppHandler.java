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
                // Indicates that the message is a status message, and should probably be displayed
                case Constants.MESSAGE_STATUS:
                    switch(msg.arg1) {
                        // Indicates that the status message is for Bluetooth
                        case Constants.BLUETOOTH:
                            switch(msg.arg2) {
                                case Constants.BT_OFF:
                                    Log.d("HANDLER_BT_OFF", "Bluetooth off");
                                    break;
                                case Constants.BT_ON:
                                    Log.d("HANDLER_BT_ON", "Bluetooth on");
                                    break;
                                case Constants.BT_DEVICE_NAME:
                                    Log.d("HANDLER_BT_DEVICE_NAME", "Device name");
                                    break;
                                case BluetoothService.STATE_NONE:
                                    Log.d("HANDLER_STATE_NONE", "Not connected");
                                    if(toolbar != null) {
                                        toolbar.setSubtitle(R.string.title_btNotConnected);
                                    }
                                    break;
                                case BluetoothService.STATE_CONNECTING:
                                    Log.d("HANDLER_STATE_CONNECTING", "Connecting...");
                                    if(toolbar != null) {
                                        toolbar.setSubtitle(R.string.title_btConnecting);
                                    }
                                    break;
                                case BluetoothService.STATE_CONNECTED:
                                    Log.d("HANDLER_STATE_CONNECTED", "Connected");
                                    if(toolbar != null) {
                                        toolbar.setSubtitle(R.string.title_btConnected);
                                    }
                                    break;
                            }
                            break;
                    }
                    break;

                // Indicates that the message is data related
                case Constants.MESSAGE_DATA:
                    switch(msg.arg1) {
                        case Constants.DATA_SEND:
                            byte[] writeBuffer = (byte[]) msg.obj;
                            String writeData = new String(writeBuffer, 0, 11);
                            Log.d("HANDLER_DATA_SEND", writeData);
                            break;
                        case Constants.DATA_RCV:
                            // TODO: ADD CODE FOR DEALING WITH MESSAGE_DATA RETURNED FROM THE CONTROLLER
                            byte[] readBuffer = (byte[]) msg.obj;
                            String readData = new String(readBuffer, 0, msg.arg2);
                            Log.d("HANDLER_DATA_RECEIVED", readData);
                            break;
                    }
                    break;

                // Indicates that a toast should be made
                case Constants.MESSAGE_TOAST:
                    switch(msg.arg1) {
                        case Constants.TOAST_SHORT:
                            Toast.makeText(activity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                            Log.d("HANDLER_TOAST_SHORT", msg.getData().getString(Constants.TOAST));
                            break;
                        case Constants.TOAST_LONG:
                            Toast.makeText(activity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_LONG).show();
                            Log.d("HANDLER_TOAST_LONG", msg.getData().getString(Constants.TOAST));
                            break;
                    }
                    break;
            }
        }
    }
}