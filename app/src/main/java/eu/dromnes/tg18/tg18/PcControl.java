package eu.dromnes.tg18.tg18;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class PcControl extends Fragment {

    private OnFragmentInteractionListener mListener;

    public PcControl() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pc_control, container, false);

        ButtonTouchListener buttonTouchListener = new ButtonTouchListener();

        ImageButton btn_onOff = view.findViewById(R.id.btn_pcOnOff);
        btn_onOff.setOnTouchListener(buttonTouchListener);
        ImageButton btn_restart = view.findViewById(R.id.btn_pcRestart);
        btn_restart.setOnTouchListener(buttonTouchListener);

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
    private class ButtonTouchListener implements Button.OnTouchListener {
        String dataToSend;

        public boolean onTouch(View view, MotionEvent event) {
            switch(view.getId()) {
                case R.id.btn_pcOnOff:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        dataToSend = Constants.PC_CONTROL + Constants.PC_ONOFF;
                        mListener.handleData(dataToSend, true);
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        view.performClick();
                        dataToSend = Constants.PC_CONTROL + Constants.PC_ONOFF_RELEASE;
                        mListener.handleData(dataToSend, true);
                    }
                    break;
                case R.id.btn_pcRestart:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        dataToSend = Constants.PC_CONTROL + Constants.PC_RESTART;
                        mListener.handleData(dataToSend, true);
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        view.performClick();
                        dataToSend = Constants.PC_CONTROL + Constants.PC_RESTART_RELEASE;
                        mListener.handleData(dataToSend, true);
                    }
                    break;
            }
            return true;
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
        void handleData(String dataToSend, boolean send);
    }
}
