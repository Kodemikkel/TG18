package eu.dromnes.tg18.tg18;

import android.app.Activity;
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
            btSettings = null;
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
                            bluetoothStateChange(DataFormatter.formatData(
                                    Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE,
                                    Constants.SYS_BT_OFF, Constants.NONE));
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if(enableBt != null) {
                                enableBt.setChecked(true);
                            }
                            bluetoothStateChange(DataFormatter.formatData(
                                    Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE,
                                    Constants.SYS_BT_ON, Constants.NONE));
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
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Constants.REQUEST_BT_ON);
            } else {
                bluetoothStateChange(DataFormatter.formatData(Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE, Constants.SYS_BT_ON, Constants.NONE));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_icons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                btSettings = new BtSettings();
                transaction.replace(R.id.content, btSettings);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
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
        String[] dataCodes = DataFormatter.readData(data);
        if(dataCodes[0].equals(Constants.INTERNAL)) {
            // TODO: ADD CODE FOR DEALING WITH BLUETOOTH ON/OFF FEEDBACK

            // Check the result for a request to enable or disable Bluetooth
            if(dataCodes[3].equals(Constants.SYS_BT_ON)) {
                handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, Constants.BT_ON).sendToTarget();
                btService.connectToController();
            } else if(dataCodes[3].equals(Constants.SYS_BT_OFF)) {
                handler.obtainMessage(Constants.MESSAGE_STATUS, Constants.BLUETOOTH, Constants.BT_OFF).sendToTarget();
                Toolbar toolbar = findViewById(R.id.toolBar);
                toolbar.setSubtitle("Bluetooth disabled - cannot connect");
            }
            // Check if there is a request to enable or disable Bluetooth
            if(dataCodes[4].equals(Constants.SYS_BT_ON)) {
                if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, Constants.REQUEST_BT_ON);
                }
            } else if (dataCodes[4].equals(Constants.SYS_BT_OFF)) {
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
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
                    bluetoothStateChange(DataFormatter.formatData(Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE, Constants.SYS_BT_ON, Constants.NONE));
                } else {
                    bluetoothStateChange(DataFormatter.formatData(Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE, Constants.SYS_BT_OFF, Constants.NONE));
                }
                break;
        }
    }
}
