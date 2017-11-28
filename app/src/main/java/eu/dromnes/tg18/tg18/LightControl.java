package eu.dromnes.tg18.tg18;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
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
    private String valueRGBA;

    private SeekBar seekBarR;
    private SeekBar seekBarG;
    private SeekBar seekBarB;
    private SeekBar seekBarA;

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
        Button btnDimUp = view.findViewById(R.id.btn_dimUp);
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
        Button btnDimDown = view.findViewById(R.id.btn_dimDown);
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
        Button btnOff = view.findViewById(R.id.btn_off);
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
        Button btnOn = view.findViewById(R.id.btn_on);
        btnOn.setOnClickListener(buttonListener);
        Button btnWhite = view.findViewById(R.id.btnC_white);
        btnWhite.setOnClickListener(buttonListener);
        Button btnFlash = view.findViewById(R.id.btn_flash);
        btnFlash.setOnClickListener(buttonListener);
        Button btnStrobe = view.findViewById(R.id.btn_strobe);
        btnStrobe.setOnClickListener(buttonListener);
        Button btnFade = view.findViewById(R.id.btn_fade);
        btnFade.setOnClickListener(buttonListener);
        Button btnSmooth = view.findViewById(R.id.btn_smooth);
        btnSmooth.setOnClickListener(buttonListener);

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

    // Listener for buttons.
    private class ButtonListener implements Button.OnClickListener {
        String dataToSend;
        String color;

        ButtonListener() {
            this.dataToSend = "0000000";
        }

        // Set the values for each of the seekBars to match the color.
        private void setSeekBarProgress(String color) {
            int valueA = Integer.parseInt(color.substring(0, 2), 16);
            int valueR = Integer.parseInt(color.substring(2, 4), 16);
            int valueG = Integer.parseInt(color.substring(4, 6), 16);
            int valueB = Integer.parseInt(color.substring(6, 8), 16);

            seekBarR.setProgress(valueR);
            seekBarG.setProgress(valueG);
            seekBarB.setProgress(valueB);
            seekBarA.setProgress(valueA);
        }

        public void onClick(View view) {
            // TODO: DEAL WITH FUNCTIONAL BUTTONS AS WELL
            // Do the appropriate action for the specific button.
            switch(view.getId()) {
                case R.id.btn_dimUp:

                    break;
                case R.id.btnC_red:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.red));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_lightRed:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightRed));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_orange:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.orange));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_lightOrange:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightOrange));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_yellow:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.yellow));
                    setSeekBarProgress(color);
                    break;
                case R.id.btn_dimDown:

                    break;
                case R.id.btnC_green:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.green));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_lightGreen:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightGreen));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_cyan:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.cyan));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_lightTurquoise:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightTurquoise));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_turquoise:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.turquoise));
                    setSeekBarProgress(color);
                    break;
                case R.id.btn_off:

                    break;
                case R.id.btnC_blue:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.blue));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_lightBlue:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.lightBlue));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_violet:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.violet));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_purple:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.purple));
                    setSeekBarProgress(color);
                    break;
                case R.id.btnC_pink:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.pink));
                    setSeekBarProgress(color);
                    break;
                case R.id.btn_on:

                    break;
                case R.id.btnC_white:
                    color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.white));
                    setSeekBarProgress(color);
                    break;
                case R.id.btn_flash:

                    break;
                case R.id.btn_strobe:

                    break;
                case R.id.btn_fade:

                    break;
                case R.id.btn_smooth:

                    break;
            }
            /*
            * Check if the button clicked is used to set a color.
            * If so, we want to send the appropriate data to the controller,
            * but only after the seekBars have had their values set.
            * This is to avoid sending data multiple times.
            */
            if(view.getTag().toString().contains("btnC_")) {
                mListener.sendData(valueRGBA);
            }
        }
    }

    // Listener for seekBars
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar){}

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
            valueRGBA = Constants.LIGHT_CONTROL + valueR + valueG + valueB + valueA;

            // Check if the change is from a user
            // If it is, we want to send the changes to the controller instantly
            if (fromUser) {
                mListener.sendData(valueRGBA);
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
