package eu.dromnes.tg18.tg18;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.nio.charset.StandardCharsets;

// TODO: FIND A WAY TO DISPLAY STATUS TO THE USER BY NOT JUST USING TOASTS

public class MainActivity extends AppCompatActivity implements LightControl.OnFragmentInteractionListener,
        HeightControl.OnFragmentInteractionListener, PcControl.OnFragmentInteractionListener, BtSettings.OnFragmentInteractionListener {

    private final static String TAG = "MainActivity";

    final AppHandler handler = new AppHandler(this);
    private BluetoothService btService = null;

    BtSettings btSettings = null;
    StatusLog statusLog = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
            btSettings = null;
            statusLog = null;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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
            }
            return false;
        }
    };

    private final BroadcastReceiver btStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action != null) {
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    SwitchPreference enableBt = null;
                    if(btSettings != null) {
                        enableBt = (SwitchPreference) btSettings.findPreference(BtSettings.KEY_PREF_ENABLE_BLUETOOTH);
                    }
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            if(enableBt != null) {
                                enableBt.setChecked(false);
                            }
                            bluetoothStateChange(Constants.INTERNAL + Constants.BT_DISABLED);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if(enableBt != null) {
                                enableBt.setChecked(true);
                            }
                            bluetoothStateChange(Constants.INTERNAL + Constants.BT_ENABLED);
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MdTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new LightControl()).commit();

        btService = new BluetoothService(handler);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean(BtSettings.KEY_PREF_AUTO_BLUETOOTH, false)) {
            bluetoothStateChange(Constants.INTERNAL + Constants.BT_TURN_ON);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_icons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: FIX BUG THAT CAUSES STATUSLOG TO BE ADDED MULTIPLE TIMES TO THE BACKSTACK
        switch(item.getItemId()) {
            case R.id.action_settings:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if(btSettings == null) {
                    btSettings = new BtSettings();
                    statusLog = new StatusLog();
                }
                transaction.replace(R.id.content, btSettings);
                transaction.addToBackStack(null);
                transaction.add(R.id.content, statusLog);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(btStateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(btStateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(btService != null) {
            btService.disconnect();
            btService = null;
        }
    }

    // Every change to the state of the Bluetooth adapter should happen through this method
    // This way we ensure that the correct actions are taken and that the application is
    // "aware" that the Bluetooth state has been modified
    public void bluetoothStateChange(String data) {
        String[] temp = data.split("_");
        String sysPrefix = temp[0] + "_";
        String dataCode = temp[1];
        if(sysPrefix.equals(Constants.INTERNAL)) {
                if(dataCode.equals(Constants.BT_ENABLED)) {
                    handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, Constants.BT_ON).sendToTarget();
                    btService.connectToController();
                }
                if(dataCode.equals(Constants.BT_DISABLED)) {
                    handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, Constants.BT_OFF).sendToTarget();
                }
                if(dataCode.equals(Constants.BT_TURN_ON)) {
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, Constants.BT_TURNED_ON).sendToTarget();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, Constants.REQUEST_BT_ON);
                    }
                }
                if(dataCode.equals(Constants.BT_TURN_OFF)) {
                    if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, Constants.BT_TURNED_OFF).sendToTarget();
                        btService.disconnect();
                        BluetoothAdapter.getDefaultAdapter().disable();
                    }
                }
        }
    }

    public void sendData(String dataToSend) {
        String[] dataCode = DataFormatter.readData(dataToSend);
        switch(dataCode[0]) {
            case Constants.INTERNAL:
            // Any data that should be dealt with internally
                break;
            case Constants.SYSTEM:
            // Any data that should be sent over Bluetooth (not the ones listed below)
                break;
            case Constants.LIGHT_CONTROL:
            // Any data that should be sent over Bluetooth and recognized as light control
                byte[] send = dataToSend.getBytes(StandardCharsets.UTF_8);
                btService.write(send);
                break;
            case Constants.HEIGHT_CONTROL:
            // Any data that should be sent over Bluetooth and recognized as height control
                break;
            case Constants.PC_CONTROL:
            // Any data that should be sent over Bluetooth and recognized as pc control
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constants.REQUEST_BT_ON:
                if(resultCode == Activity.RESULT_OK) {
                    bluetoothStateChange(Constants.INTERNAL + Constants.BT_ENABLED);
                } else {
                    bluetoothStateChange(Constants.INTERNAL + Constants.BT_DISABLED);
                }
                break;
        }
    }
}
