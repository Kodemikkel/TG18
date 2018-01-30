package eu.dromnes.tg18.tg18;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

public class LightControl extends Fragment {

    private String valueR = "00";
    private String valueG = "00";
    private String valueB = "00";
    private String valueA = "00";
    private String functionCode = null;

    private SeekBar seekBarR;
    private SeekBar seekBarG;
    private SeekBar seekBarB;
    private SeekBar seekBarA;

    private boolean seekBarsEnabled = true;

    private OnFragmentInteractionListener mListener;

    public LightControl() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_light_control, container, false);

        if(savedInstanceState != null) {
            valueR = savedInstanceState.getString("valueR");
            valueG = savedInstanceState.getString("valueG");
            valueB = savedInstanceState.getString("valueB");
            valueA = savedInstanceState.getString("valueA");
        }

        // Create a listener for buttons and seekBars.
        ButtonListener buttonListener = new ButtonListener();
        SeekBarListener seekBarListener = new SeekBarListener();

        // Set the onChangeListener for all seekBars to the one created previously.
        seekBarR = view.findViewById(R.id.sldr_r);
        seekBarG = view.findViewById(R.id.sldr_g);
        seekBarB = view.findViewById(R.id.sldr_b);
        seekBarA = view.findViewById(R.id.sldr_a);
        seekBarR.setOnSeekBarChangeListener(seekBarListener);
        seekBarG.setOnSeekBarChangeListener(seekBarListener);
        seekBarB.setOnSeekBarChangeListener(seekBarListener);
        seekBarA.setOnSeekBarChangeListener(seekBarListener);

        // Set the onClickListener for all buttons to the one created previously.
        Button btnDimUp = view.findViewById(R.id.btnF_dimUp);
        btnDimUp.setOnClickListener(buttonListener);
        Button btnRed = view.findViewById(R.id.btnC_red);
        btnRed.setOnClickListener(buttonListener);
        Button btnLightRed = view.findViewById(R.id.btnC_lightRed);
        btnLightRed.setOnClickListener(buttonListener);
        Button btnOrange = view.findViewById(R.id.btnC_orange);
        btnOrange.setOnClickListener(buttonListener);
        Button btnLightOrange = view.findViewById(R.id.btnC_lightOrange);
        btnLightOrange.setOnClickListener(buttonListener);
        Button btnYellow = view.findViewById(R.id.btnC_yellow);
        btnYellow.setOnClickListener(buttonListener);
        Button btnDimDown = view.findViewById(R.id.btnF_dimDown);
        btnDimDown.setOnClickListener(buttonListener);
        Button btnGreen = view.findViewById(R.id.btnC_green);
        btnGreen.setOnClickListener(buttonListener);
        Button btnLightGreen = view.findViewById(R.id.btnC_lightGreen);
        btnLightGreen.setOnClickListener(buttonListener);
        Button btnCyan = view.findViewById(R.id.btnC_cyan);
        btnCyan.setOnClickListener(buttonListener);
        Button btnLightTurquoise = view.findViewById(R.id.btnC_lightTurquoise);
        btnLightTurquoise.setOnClickListener(buttonListener);
        Button btnTurquoise = view.findViewById(R.id.btnC_turquoise);
        btnTurquoise.setOnClickListener(buttonListener);
        Button btnOff = view.findViewById(R.id.btnF_off);
        btnOff.setOnClickListener(buttonListener);
        Button btnBlue = view.findViewById(R.id.btnC_blue);
        btnBlue.setOnClickListener(buttonListener);
        Button btnLightBlue = view.findViewById(R.id.btnC_lightBlue);
        btnLightBlue.setOnClickListener(buttonListener);
        Button btnViolet = view.findViewById(R.id.btnC_violet);
        btnViolet.setOnClickListener(buttonListener);
        Button btnPurple = view.findViewById(R.id.btnC_purple);
        btnPurple.setOnClickListener(buttonListener);
        Button btnPink = view.findViewById(R.id.btnC_pink);
        btnPink.setOnClickListener(buttonListener);
        Button btnOn = view.findViewById(R.id.btnF_on);
        btnOn.setOnClickListener(buttonListener);
        Button btnWhite = view.findViewById(R.id.btnC_white);
        btnWhite.setOnClickListener(buttonListener);
        Button btnFlash = view.findViewById(R.id.btnF_flash);
        btnFlash.setOnClickListener(buttonListener);
        Button btnStrobe = view.findViewById(R.id.btnF_strobe);
        btnStrobe.setOnClickListener(buttonListener);
        Button btnFade = view.findViewById(R.id.btnF_fade);
        btnFade.setOnClickListener(buttonListener);
        Button btnSmooth = view.findViewById(R.id.btnF_smooth);
        btnSmooth.setOnClickListener(buttonListener);

        enableSeekBars(seekBarsEnabled);

        return view;
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

    // Set the values for each of the seekBars to match the color.
    private void setSeekBarProgress(String valR, String valG, String valB, String valA) {
        int valueR = Integer.parseInt(valR, 16);
        int valueG = Integer.parseInt(valG, 16);
        int valueB = Integer.parseInt(valB, 16);
        int valueA = Integer.parseInt(valA, 16);

        seekBarR.setProgress(valueR);
        seekBarG.setProgress(valueG);
        seekBarB.setProgress(valueB);
        seekBarA.setProgress(valueA);
    }

    private void enableSeekBars(boolean enable) {
        seekBarsEnabled = enable;
        seekBarR.setEnabled(enable);
        seekBarG.setEnabled(enable);
        seekBarB.setEnabled(enable);
    }

    // Listener for buttons.
    private class ButtonListener implements Button.OnClickListener {
        String buttonColor = "00000000";
        int alphaStep = 32;
        int alphaInt = 0;


        public void onClick(View view) {
            // TODO: DEAL WITH ON/OFF BUTTONS
            // Do the appropriate action for the specific button.
            switch(view.getId()) {
                case R.id.btnF_dimUp:
                    alphaInt = Integer.parseInt(valueA, 16);
                    if(alphaInt < 255) {
                        if(alphaInt + alphaStep > 255) {
                            alphaInt = 255;
                        } else {
                            alphaInt += alphaStep;
                        }
                        valueA = String.format("%02X", (0xFF & alphaInt));
                    }
                    break;
                case R.id.btnC_red:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.red));
                    break;
                case R.id.btnC_lightRed:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightRed));
                    break;
                case R.id.btnC_orange:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.orange));
                    break;
                case R.id.btnC_lightOrange:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightOrange));
                    break;
                case R.id.btnC_yellow:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.yellow));
                    break;
                case R.id.btnF_dimDown:
                    alphaInt = Integer.parseInt(valueA, 16);
                    if(alphaInt > 0) {
                        if(alphaInt - alphaStep < 0) {
                            alphaInt = 0;
                        } else {
                            alphaInt -= alphaStep;
                        }
                        valueA = String.format("%02X", (0xFF & alphaInt));
                    }
                    break;
                case R.id.btnC_green:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.green));
                    break;
                case R.id.btnC_lightGreen:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightGreen));
                    break;
                case R.id.btnC_cyan:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.cyan));
                    break;
                case R.id.btnC_lightTurquoise:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightTurquoise));
                    break;
                case R.id.btnC_turquoise:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.turquoise));
                    break;
                case R.id.btnF_off:

                    break;
                case R.id.btnC_blue:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.blue));
                    break;
                case R.id.btnC_lightBlue:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightBlue));
                    break;
                case R.id.btnC_violet:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.violet));
                    break;
                case R.id.btnC_purple:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.purple));
                    break;
                case R.id.btnC_pink:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.pink));
                    break;
                case R.id.btnF_on:

                    break;
                case R.id.btnC_white:
                    buttonColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.white));
                    break;
                case R.id.btnF_flash:
                    functionCode = Constants.LT_FLASH;
                    break;
                case R.id.btnF_strobe:
                    functionCode = Constants.LT_STROBE;
                    break;
                case R.id.btnF_fade:
                    functionCode = Constants.LT_FADE;
                    break;
                case R.id.btnF_smooth:
                    functionCode = Constants.LT_SMOOTH;
                    break;
            }
            /*
            * Check if the button clicked is used to set a color.
            * If so, we want to send the appropriate data to the controller,
            * but only after the seekBars have had their values set.
            * This is to avoid sending data multiple times.
            */

            if(view.getTag().toString().contains("btnC_")) {
                enableSeekBars(true);
                functionCode = null;
                valueR = buttonColor.substring(2, 4);
                valueG = buttonColor.substring(4, 6);
                valueB = buttonColor.substring(6, 8);

                setSeekBarProgress(valueR, valueG, valueB, valueA);
                Log.d("DATATOSEND", Constants.LIGHT_CONTROL + valueR + valueG + valueB + valueA);
                mListener.sendData(Constants.LIGHT_CONTROL + valueR + valueG + valueB + valueA);
            } else if(view.getTag().toString().contains("btnF_")) {
                if(functionCode == null) {
                    enableSeekBars(true);
                    setSeekBarProgress(valueR, valueG, valueB, valueA);
                    Log.d("DATATOSEND", Constants.LIGHT_CONTROL + valueR + valueG + valueB + valueA);
                    mListener.sendData(Constants.LIGHT_CONTROL + valueR + valueG + valueB + valueA);
                } else {
                    enableSeekBars(false);
                    valueR = functionCode;
                    valueG = "";
                    valueB = "";
                    seekBarA.setProgress(Integer.parseInt(valueA, 16));
                    Log.d("DATATOSEND", Constants.LIGHT_CONTROL + functionCode + valueA);
                    mListener.sendData(Constants.LIGHT_CONTROL + functionCode + valueA);
                }
            }
        }
    }

    // Listener for seekBars
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar){}

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                switch (seekBar.getId()) {
                    case R.id.sldr_r:
                        valueR = String.format("%02X", (0xFF & progress));
                        break;
                    case R.id.sldr_g:
                        valueG = String.format("%02X", (0xFF & progress));
                        break;
                    case R.id.sldr_b:
                        valueB = String.format("%02X", (0xFF & progress));
                        break;
                    case R.id.sldr_a:
                        valueA = String.format("%02X", (0xFF & progress));
                        break;
                }
                Log.d("DATATOSEND", Constants.LIGHT_CONTROL + valueR + valueG + valueB + valueA);
                mListener.sendData(Constants.LIGHT_CONTROL + valueR + valueG + valueB + valueA);
            }
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
        void sendData(String dataToSend);
    }
}
