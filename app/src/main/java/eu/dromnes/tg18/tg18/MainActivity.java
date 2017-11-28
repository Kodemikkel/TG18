package eu.dromnes.tg18.tg18;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements LightControl.OnFragmentInteractionListener,
        HeightControl.OnFragmentInteractionListener, PcControl.OnFragmentInteractionListener, BtSettings.OnFragmentInteractionListener {

    private final static String TAG = "MainActivity";

    BluetoothService btService;
    private final AppHandler handler = new AppHandler(this);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        // TODO: FIX PROPER ICONS FOR NAVIGATION AND PUT SETTINGS IN A DIFFERENT PLACE

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_lightControl:
                    transaction.replace(R.id.content, new LightControl()).commit();
                    return true;
                case R.id.navigation_heightControl:
                    transaction.replace(R.id.content, new HeightControl()).commit();
                    return true;
                case R.id.navigation_pcControl:
                    transaction.replace(R.id.content, new PcControl()).commit();
                    return true;
                case R.id.navigation_btSettings:
                    transaction.replace(R.id.content, new BtSettings()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.mdTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new LightControl()).commit();

        btService = new BluetoothService(handler);
    }

    private static class AppHandler extends Handler {
        private final WeakReference<MainActivity> activity;

        AppHandler(MainActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = this.activity.get();
            if(activity != null) {
                switch(msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        // TODO: ADD CODE FOR DEALING WITH CHANGES IN CONNECTION STATE
                        Log.d("DEBUG", "STATE_CHANGE");
                        switch(msg.arg1) {
                            case BluetoothService.STATE_NONE:
                                Log.d("DEBUG", "STATE_NONE");
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                Log.d("DEBUG", "STATE_CONNECTING");
                                break;
                            case BluetoothService.STATE_CONNECTED:
                                Log.d("DEBUG", "STATE_CONNECTED");
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

    public void onFragmentInteraction(Uri uri) {
        // TODO: FIGURE OUT HOW WE SHOULD DEAL WITH FRAGMENT INTERACTIONS
    }

    public void bluetoothStateChange(String data) {
        if(data.substring(0, 3).equals(Constants.INTERNAL)) {
            // TODO: ADD CODE FOR DEALING WITH BLUETOOTH ON/OFF FEEDBACK
            if(data.substring(data.length()-2, data.length()).equals("11")) {
                btService.connectToController();
            } else if(data.substring(data.length()-2, data.length()).equals("01")) {

            }
        }
    }

    public void lightButtonPressed(String dataToSend) {
        if(dataToSend.substring(0, 3).equals(Constants.LIGHT_CONTROL)) {
            byte[] send = dataToSend.getBytes(StandardCharsets.UTF_8);
            Log.d("DEBUG", Integer.toString(send.length));
            btService.write(send);
        } else {
            /*
             * TODO: ADD CODE FOR DEALING WITH OTHER DATA SENT FROM LIGHT CONTROL PAGE?
             * IF NOT, REWORK INTERACTION METHOD(S)
            */
        }
    }
}
