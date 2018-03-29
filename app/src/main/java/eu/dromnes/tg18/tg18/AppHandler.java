package eu.dromnes.tg18.tg18;

import android.os.Handler;
import android.os.Message;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import eu.dromnes.tg18.tg18.R.drawable;

import java.lang.ref.WeakReference;

class AppHandler extends Handler {
    private final WeakReference<MainActivity> activity;
    final static String FILENAME = "tg18_status_log";

    AppHandler(MainActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        MainActivity activity = this.activity.get();
        if(activity != null) {
            ImageView btConnected = activity.findViewById(R.id.bt_connected);
            ImageView btConnecting = activity.findViewById(R.id.bt_searching);
            ImageView btDisconnected = activity.findViewById(R.id.bt_disabled);

            switch(msg.what) {
                // Indicates that the message is a status message
                case Constants.MESSAGE_STATUS:
                    switch(msg.arg1) {
                        // Indicates that the status message is for Bluetooth
                        case Constants.BLUETOOTH:
                            switch(msg.arg2) {
                                case Constants.BT_TURNED_ON:
                                    Log.d("HANDLER_BT_TURNED_ON", "Bluetooth turned on");
                                    break;
                                case Constants.BT_TURNED_OFF:
                                    Log.d("HANDLER_BT_TURNED_OFF", "Bluetooth turned off");
                                    break;
                                case Constants.BT_OFF:
                                    Log.d("HANDLER_BT_OFF", "Bluetooth disabled");
                                    break;
                                case Constants.BT_ON:
                                    Log.d("HANDLER_BT_ON", "Bluetooth enabled");
                                    break;
                                case Constants.BT_DEVICE_NAME:
                                    String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                                    Log.d("HANDLER_BT_DEVICE_NAME", deviceName);
                                    break;
                                case BtService.STATE_SEARCHING:
                                    Log.d("HANDLER_STATE_SEARCHING", "Searching...");
                                    btDisconnected.setVisibility(View.GONE);
                                    btConnected.setVisibility(View.GONE);
                                    btConnecting.setVisibility(View.VISIBLE);
                                    break;
                                case BtService.STATE_CONNECTING:
                                    Log.d("HANDLER_STATE_CONNECTING", "Connecting...");
                                    btDisconnected.setVisibility(View.GONE);
                                    btConnected.setVisibility(View.GONE);
                                    btConnecting.setVisibility(View.VISIBLE);
                                    break;
                                case BtService.STATE_CONNECTED:
                                    Log.d("HANDLER_STATE_CONNECTED", "Connected");
                                    btDisconnected.setVisibility(View.GONE);
                                    btConnecting.setVisibility(View.GONE);
                                    btConnected.setVisibility(View.VISIBLE);
                                    break;
                                case BtService.CONNECTION_FAILED:
                                    Log.d("HANDLER_CONNECTION_FAILED", "Connection failed");
                                    btConnected.setVisibility(View.GONE);
                                    btConnecting.setVisibility(View.GONE);
                                    btDisconnected.setVisibility(View.VISIBLE);
                                    break;
                                case BtService.CONNECTION_LOST:
                                    Log.d("HANDLER_CONNECTION_LOST", "Connection lost");
                                    btConnected.setVisibility(View.GONE);
                                    btConnecting.setVisibility(View.GONE);
                                    btDisconnected.setVisibility(View.VISIBLE);
                                    break;
                                case BtService.DISCONNECTED:
                                    Log.d("HANDLER_DISCONNECTED", "Disconnected");
                                    btConnected.setVisibility(View.GONE);
                                    btConnecting.setVisibility(View.GONE);
                                    btDisconnected.setVisibility(View.VISIBLE);
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
                            activity.handleData(readData, false);
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