package eu.dromnes.tg18.tg18;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

// TODO: IF POSSIBLE; GET A CLEANER WAY OF DEALING WITH PREFERENCES

public class BtSettings extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "BtSettings";

    public static final String KEY_PREF_AUTO_BLUETOOTH = "pref_autoBluetooth";
    public static final String KEY_PREF_ENABLE_BLUETOOTH = "pref_enableBluetooth";

    private SwitchPreference enableBluetoothSwitch;

    private OnFragmentInteractionListener mListener;

    private final BroadcastReceiver btStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action != null) {
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            enableBluetoothSwitch.setChecked(false);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            enableBluetoothSwitch.setChecked(true);
                            break;
                    }
                }
            }
        }
    };

    public BtSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        enableBluetoothSwitch = (SwitchPreference) findPreference(KEY_PREF_ENABLE_BLUETOOTH);
        enableBluetoothSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Boolean value = (Boolean) newValue;
                changeBtState(value);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(btStateReceiver, filter);

        enableBluetoothSwitch.setChecked(BluetoothAdapter.getDefaultAdapter().isEnabled());
        enableBluetoothSwitch.setEnabled(!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(KEY_PREF_AUTO_BLUETOOTH, false));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        getActivity().unregisterReceiver(btStateReceiver);
    }

    void changeBtState(boolean enable) {
        if (enable) {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Constants.REQUEST_BT_ON);
            }
        } else {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                BluetoothAdapter.getDefaultAdapter().disable();
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key) {
            case KEY_PREF_AUTO_BLUETOOTH:
                if(sharedPreferences.getBoolean(key, false)) {
                    changeBtState(true);
                    enableBluetoothSwitch.setEnabled(false);
                } else {
                    enableBluetoothSwitch.setEnabled(true);
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constants.REQUEST_BT_ON:
                String bluetoothState;
                if(resultCode == Activity.RESULT_OK) {
                    bluetoothState = DataFormatter.formatData(Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE, Constants.SYS_BT_R_TURNEDON, Constants.NONE);
                } else {
                    bluetoothState = DataFormatter.formatData(Constants.INTERNAL, Constants.SYS_BLUETOOTH, Constants.NONE, Constants.SYS_BT_R_NTURNEDON, Constants.NONE);
                }
                mListener.bluetoothStateChange(bluetoothState);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void bluetoothStateChange(String data);
    }
}
