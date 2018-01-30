package eu.dromnes.tg18.tg18;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

import java.util.List;

public class BtSettings extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "BtSettings";

    public static final String KEY_PREF_AUTO_BLUETOOTH = "pref_autoBluetooth";
    public static final String KEY_PREF_ENABLE_BLUETOOTH = "pref_enableBluetooth";
    public static final String KEY_PREF_SELECT_PI = "pref_selectPi";

    private SwitchPreference enableBluetoothSwitch;
    private ListPreference listPref;

    private OnFragmentInteractionListener mListener;


    public BtSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        listPref = (ListPreference)findPreference(KEY_PREF_SELECT_PI);
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

        enableBluetoothSwitch.setChecked(BluetoothAdapter.getDefaultAdapter().isEnabled());
        listPref.setTitle(listPref.getEntry());
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    void changeBtState(boolean enable) {
        String bluetoothState;
        if (enable) {
            bluetoothState = Constants.INTERNAL + Constants.BT_TURN_ON;
        } else {
            bluetoothState = Constants.INTERNAL + Constants.BT_TURN_OFF;
        }
        mListener.bluetoothStateHandler(bluetoothState);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key) {
            case KEY_PREF_SELECT_PI:
                listPref.setTitle(listPref.getEntry());
                mListener.changeRPi(listPref.getValue());
                Log.d("RPIADDRESS", listPref.getValue());
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
        void bluetoothStateHandler(String data);
        void changeRPi(String address);
    }
}
