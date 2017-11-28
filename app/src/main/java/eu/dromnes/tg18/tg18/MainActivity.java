package eu.dromnes.tg18.tg18;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements LightControl.OnFragmentInteractionListener,
        HeightControl.OnFragmentInteractionListener, PcControl.OnFragmentInteractionListener, BtSettings.OnFragmentInteractionListener {

    private final static String TAG = "MainActivity";

    final AppHandler handler = new AppHandler(this);
    private BluetoothService btService = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
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

        // TODO: MAKE THE AUTOBLUETOOTH OPTION ENABLE BLUETOOTH AND CONNECT BY DEFAULT
        /*
         * The problem is that leaving and entering the app creates a new BluetoothService instance.
         * This means that the previously created one will be overwritten, resulting in lost connection.
         * To fix this, I've tried creating only one instance of BluetoothService, and this made the connection
         * persistent even when leaving and entering the app.
         * However, a new AppHandler instance is also created, this led to data not being received by the phone,
         * although sending data worked.
         *
         * Reverted changes to only implement the original function, which creates an instance of BluetoothService
         * but does not connect automatically until Bluetooth is enabled by the user in the app.
         */
        btService = new BluetoothService(handler);
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
                transaction.replace(R.id.content, new BtSettings());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bluetoothStateChange(String data) {
        String[] dataCodes = DataFormatter.readData(data);
        if(dataCodes[0].equals(Constants.INTERNAL)) {
            // TODO: ADD CODE FOR DEALING WITH BLUETOOTH ON/OFF FEEDBACK
            if(dataCodes[3].equals(Constants.SYS_BT_R_TURNEDON)) {
                btService.connectToController();
            } else if(dataCodes[3].equals(Constants.SYS_BT_R_NTURNEDON)) {

            }
        }
    }

    public void sendData(String dataToSend) {
        String[] dataCode = DataFormatter.readData(dataToSend);
        switch(dataCode[0]) {
            case Constants.INTERNAL:

                break;
            case Constants.SYSTEM:

                break;
            case Constants.LIGHT_CONTROL:
                byte[] send = dataToSend.getBytes(StandardCharsets.UTF_8);
                btService.write(send);
                break;
            case Constants.HEIGHT_CONTROL:

                break;
            case Constants.PC_CONTROL:

                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constants.REQUEST_BT_ON:
                if(resultCode == Activity.RESULT_OK) {
                    bluetoothStateChange(DataFormatter.formatData(Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE, Constants.SYS_BT_R_TURNEDON, Constants.NONE));
                } else {
                    bluetoothStateChange(DataFormatter.formatData(Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE, Constants.SYS_BT_R_NTURNEDON, Constants.NONE));
                }
                break;
        }
    }
}
