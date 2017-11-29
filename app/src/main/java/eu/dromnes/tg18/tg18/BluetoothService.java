package eu.dromnes.tg18.tg18;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

// TODO: CHANGE THE MESSAGES SENT TO THE HANDLER SO THAT WE CAN UPDATE THE UI INSTEAD OF JUST SHOWING TOASTS

class BluetoothService {
    private static final String TAG = "BluetoothService";

    private static final String PI_BD_ADDRESS = "B8:27:EB:41:BD:A0";
    //private static final UUID UNIQUE_ID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
    private static final UUID UNIQUE_ID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static final int STATE_NONE = 0;
    static final int STATE_CONNECTING = 1;
    static final int STATE_CONNECTED = 2;

    private Handler handler;

    private final BluetoothAdapter deviceBtAdapter;
    private BluetoothDevice controller;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private int currentState;

    BluetoothService(Handler handler) {
        this.deviceBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // Check for Bluetooth compatibility.
        if(deviceBtAdapter == null) {
            // TODO: ADD CODE FOR NON-COMPATIBLE DEVICES
            Log.e(TAG, "Not compatible with Bluetooth.");
        } else {
            this.handler = handler;
            this.currentState = STATE_NONE;
        }
    }

    private void changeState(int state) {
        this.currentState = state;
        handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    private void findController(String address) {
        // Get a list of all devices paired with this device.
        Set<BluetoothDevice> pairedDevices = deviceBtAdapter.getBondedDevices();

        // Iterate through the list of paired devices to find the correct device.
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                String serverBDAddress = device.getAddress();
                if(serverBDAddress.equals(address)) {
                    this.controller = device;
                    break;
                }
            }
        }
    }

    void connectToController() {
        findController(PI_BD_ADDRESS);
        connectThread = new ConnectThread();
        connectThread.start();
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
        if(currentState == STATE_CONNECTED) {
            connectedThread.cancel();
        } else if(currentState == STATE_CONNECTING) {
            connectThread.cancel();
        }
        currentState = STATE_NONE;
    }

    private void connectionFailed() {
        // Send a message to the handler to tell that the connection failed.
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect to server");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Set the current state to none, as we are neither connecting nor connected.
        changeState(STATE_NONE);
    }

    private void connectionLost() {
        // Send a message to the handler to tell that the connection was lost.
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Connection lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Set the current state to none, as we are neither connecting nor connected.
        changeState(STATE_NONE);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;

        ConnectThread() {
            BluetoothSocket tmp = null;

            try {
                tmp = controller.createRfcommSocketToServiceRecord(UNIQUE_ID);
            } catch(IOException socketException) {
                Log.e(TAG, "Failed to create socket", socketException);
            }
            socket = tmp;
            changeState(STATE_CONNECTING);
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
            }
            synchronized (BluetoothService.this) {
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

            while(true) {
                try {
                    numBytes = inStream.read(buffer);

                    handler.obtainMessage(Constants.MESSAGE_READ, numBytes, -1, buffer).sendToTarget();
                } catch(IOException connectionException) {
                    Log.e(TAG, "Input stream was disconnected", connectionException);
                    connectionLost();
                    break;
                }
            }
        }

        void write(byte[] buffer) {
            try {
                outStream.write(buffer);

                handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch(IOException writeException) {
                Log.e(TAG, "Error occurred while writing data", writeException);
                handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
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