package eu.dromnes.tg18.tg18;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

class BtService {
    private static final String TAG = "BtService";

    //private static final String PI_BD_ADDRESS = "B8:27:EB:41:BD:A0";
    private static final String PI_BD_ADDRESS = "B8:27:EB:14:BF:22";
    //private static final UUID UNIQUE_ID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
    private static final UUID UNIQUE_ID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static final int STATE_NONE = 0;
    static final int STATE_SEARCHING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int CONNECTION_FAILED = 4;
    static final int CONNECTION_LOST = 5;
    static final int DISCONNECTED = 6;

    private final Handler handler;

    private final BluetoothAdapter deviceBtAdapter;
    private BluetoothDevice controller;
    private String controllerName;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private int currentState;
    private boolean userDisconnect;

    BtService(Handler handler) {
        this.deviceBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // Check for Bluetooth compatibility.
        if(deviceBtAdapter == null) {
            // TODO: ADD CODE FOR NON-COMPATIBLE DEVICES
            Log.e(TAG, "Not compatible with Bluetooth.");
        }
        this.handler = handler;
        this.currentState = STATE_NONE;
    }

    int getState() {
        return currentState;
    }

    synchronized private void changeState(int state) {
        this.currentState = state;
        handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, state).sendToTarget();
    }

    synchronized private void findController(String address) {
        if(controller != null) {
            controller = null;
            controllerName = null;
        }
        // Get a list of all devices paired with this device.
        Set<BluetoothDevice> pairedDevices = deviceBtAdapter.getBondedDevices();

        // Iterate through the list of paired devices to find the correct device.
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                String serverBDAddress = device.getAddress();
                if(serverBDAddress.equals(address)) {
                    this.controller = device;
                    this.controllerName = device.getName();
                    break;
                }
            }
        }
    }

    synchronized void connectToController(String address) {
        if(currentState == STATE_CONNECTING) {
            if(connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }
        if(connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        Log.d("CONNECT", "Connecting to " + address);

        // Send a message to the handler to tell that we are trying to connect.
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST, Constants.TOAST_SHORT, -1);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Connecting...");
        msg.setData(bundle);
        handler.sendMessage(msg);

        changeState(STATE_SEARCHING);
        userDisconnect = false;

        findController(address);
        if(controller != null) {
            connectThread = new ConnectThread();
            connectThread.start();
        } else {
            connectionFailed();
        }
    }

    private synchronized void connected(BluetoothSocket socket) {
        if(connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if(connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Send a message to the handler to tell that we are connected.
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST, Constants.TOAST_SHORT, -1);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Connected");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Send a message to the handler containing the device name.
        Message msg1 = handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, Constants.BT_DEVICE_NAME);
        Bundle bundle1 = new Bundle();
        bundle1.putString(Constants.DEVICE_NAME, controllerName);
        msg1.setData(bundle1);
        handler.sendMessage(msg1);

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    void write(byte[] data) {
        ConnectedThread ref;

        synchronized (this) {
            if(currentState != STATE_CONNECTED) {
                return;
            }
            ref = connectedThread;
        }
        ref.write(data);
    }

    void disconnect() {
        userDisconnect = true;
        currentState = STATE_NONE;
        if(connectedThread != null || connectThread != null) {
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
            handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, DISCONNECTED).sendToTarget();
        }
    }

    private void connectionFailed() {
        // Send a message to the handler to tell that the connection failed.
        handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, CONNECTION_FAILED).sendToTarget();

        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST, Constants.TOAST_SHORT, -1);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect to server");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Set the current state to none, as we are neither connecting nor connected.
        currentState = STATE_NONE;
    }

    private void connectionLost() {
        // Send a message to the handler to tell that the connection was lost.
        handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, CONNECTION_LOST).sendToTarget();

        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST, Constants.TOAST_SHORT, -1);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Connection lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Set the current state to none, as we are neither connecting nor connected.
        currentState = STATE_NONE;
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;

        ConnectThread() {
            changeState(STATE_CONNECTING);
            BluetoothSocket tmp = null;

            try {
                tmp = controller.createRfcommSocketToServiceRecord(UNIQUE_ID);
            } catch(IOException socketException) {
                Log.e(TAG, "Failed to create socket", socketException);
            }
            socket = tmp;
        }

        public void run() {
            deviceBtAdapter.cancelDiscovery();

            try  {
                socket.connect();
            } catch(IOException connectException) {
                try {
                    socket.close();
                } catch(IOException closeException) {
                    Log.e(TAG, "Unable to close socket", closeException);
                }
                connectionFailed();
                return;
            }
            synchronized (BtService.this) {
                connectThread = null;
            }
            connected(socket);
        }

        void cancel() {
            try {
                socket.close();
            } catch(IOException closeException) {
                Log.e(TAG, "Unable to close socket", closeException);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inStream;
        private final OutputStream outStream;
        private byte[] buffer;

        ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch(IOException inputStreamException) {
                Log.e(TAG, "Error occurred when creating input stream", inputStreamException);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch(IOException outputStreamException) {
                Log.e(TAG, "Error occurred when creating output stream", outputStreamException);
            }

            inStream = tmpIn;
            outStream = tmpOut;
            changeState(STATE_CONNECTED);
        }

        public void run() {
            buffer = new byte[1024];
            int numBytes;

            while(currentState == STATE_CONNECTED) {
                try {
                    numBytes = inStream.read(buffer);

                    handler.obtainMessage(Constants.MESSAGE_DATA, Constants.DATA_RCV, numBytes, buffer).sendToTarget();
                } catch(IOException connectionException) {
                    Log.e(TAG, "Input stream was disconnected", connectionException);
                    if(!userDisconnect) {
                        Log.d("DISCONNECT", "The user did NOT disconnect (input)");
                        connectionLost();
                    }
                    break;
                }
            }
        }

        void write(byte[] buffer) {
            try {
                outStream.write(buffer);

                handler.obtainMessage(Constants.MESSAGE_DATA, Constants.DATA_SEND, -1, buffer).sendToTarget();
            } catch(IOException writeException) {
                Log.e(TAG, "Error occurred while writing data", writeException);
                if(!userDisconnect) {
                    Log.d("DISCONNECT", "The user did NOT disconnect (output)");
                    connectionLost();
                }
            }
        }

        void cancel() {
            try {
                socket.close();
            } catch(IOException closeException) {
                Log.e(TAG, "Could not close the connection socket", closeException);
            }
        }
    }
}