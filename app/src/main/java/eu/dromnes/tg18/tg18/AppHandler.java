package eu.dromnes.tg18.tg18;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Map;

class AppHandler extends Handler {
    private final WeakReference<MainActivity> activity;
    private String logEntry = "This is a test for status log\n";
    final static String FILENAME = "tg18_status_log";

    AppHandler(MainActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        MainActivity activity = this.activity.get();
        if(activity != null) {
            TextView statusLog = activity.findViewById(R.id.statusLog);

            switch(msg.what) {
                // Indicates that the message is a status message, and should probably be displayed
                case Constants.MESSAGE_STATUS:
                    FileOutputStream outputStream = null;
                    String textToLog = "Status message";
                    try {
                        FileInputStream inputStream = activity.openFileInput(FILENAME);
                        InputStreamReader reader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        int lines = 0;
                        while (bufferedReader.readLine() != null) {
                            lines++;
                        }
                        outputStream = ((lines > 15) ? activity.openFileOutput(FILENAME, Context.MODE_PRIVATE) :
                                activity.openFileOutput(FILENAME, Context.MODE_APPEND));
                    } catch (FileNotFoundException fileException) {
                        try {
                            activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                        } catch (FileNotFoundException newFileException) {
                            Log.e("FILEEXCEPTION", "Failed to create file " + newFileException);
                        }
                    } catch (IOException ioException) {
                        Log.e("IOEXCEPTION", "Failed to read line " + ioException);
                    }

                    switch(msg.arg1) {
                        // Indicates that the status message is for Bluetooth
                        case Constants.BLUETOOTH:
                            switch(msg.arg2) {
                                case Constants.BT_TURNED_ON:
                                    Log.d("HANDLER_BT_TURNED_ON", "Bluetooth turned on");
                                    textToLog = "Bluetooth turned on";
                                    break;
                                case Constants.BT_TURNED_OFF:
                                    Log.d("HANDLER_BT_TURNED_OFF", "Bluetooth turned off");
                                    textToLog = "Bluetooth turned off";
                                    break;
                                case Constants.BT_OFF:
                                    Log.d("HANDLER_BT_OFF", "Bluetooth disabled");
                                    textToLog = "Bluetooth disabled";
                                    break;
                                case Constants.BT_ON:
                                    Log.d("HANDLER_BT_ON", "Bluetooth enabled");
                                    textToLog = "Bluetooth enabled";
                                    break;
                                case Constants.BT_DEVICE_NAME:
                                    String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                                    Log.d("HANDLER_BT_DEVICE_NAME", "Device name");
                                    textToLog = "Device name: " + deviceName;
                                    break;
                                case BluetoothService.STATE_NONE:
                                    Log.d("HANDLER_STATE_NONE", "Not connected");
                                    textToLog = "Not connected";
                                    break;
                                case BluetoothService.STATE_CONNECTING:
                                    Log.d("HANDLER_STATE_CONNECTING", "Connecting...");
                                    textToLog = "Connecting...";
                                    break;
                                case BluetoothService.STATE_CONNECTED:
                                    Log.d("HANDLER_STATE_CONNECTED", "Connected");
                                    textToLog = "Connected";
                                    break;
                            }
                            break;
                    }
                    textToLog = textToLog + "\n";
                    if(outputStream != null) {
                        try {
                            outputStream.write((textToLog).getBytes());
                            outputStream.close();
                        } catch (Exception fileException) {
                            Log.e("FILEEXCEPTION", "Failed closing output stream " + fileException);
                        }
                    }
                    if(statusLog != null) {
                        if (statusLog.getLayout().getLineCount() > 15) {
                            statusLog.setText("");
                        }
                        statusLog.append(textToLog);
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