package eu.dromnes.tg18.tg18;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class HeightControl extends Fragment {

    private OnFragmentInteractionListener mListener;

    public HeightControl() {
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
        View view = inflater.inflate(R.layout.fragment_height_control, container, false);

        // Create a listener for buttons.
        ButtonListener buttonListener = new ButtonListener();
        ButtonTouchListener buttonTouchListener = new ButtonTouchListener();

        ImageButton btn_top = view.findViewById(R.id.btn_topPosition);
        btn_top.setOnClickListener(buttonListener);
        ImageButton btn_up = view.findViewById(R.id.btn_up);
        btn_up.setOnTouchListener(buttonTouchListener);
        ImageButton btn_stop = view.findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(buttonListener);
        ImageButton btn_down = view.findViewById(R.id.btn_down);
        btn_down.setOnTouchListener(buttonTouchListener);
        ImageButton btn_bottom = view.findViewById(R.id.btn_bottomPosition);
        btn_bottom.setOnClickListener(buttonListener);

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

        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_topPosition:
                    dataToSend = Constants.HEIGHT_CONTROL + Constants.HT_TOP;
                    break;
                case R.id.btn_stop:
                    dataToSend = Constants.HEIGHT_CONTROL + Constants.HT_STOP;
                    break;
                case R.id.btn_bottomPosition:
                    dataToSend = Constants.HEIGHT_CONTROL + Constants.HT_BOTTOM;
                    break;
            }
            Log.d("DATATOSEND (CLICK)", dataToSend);
            mListener.sendData(dataToSend);
        }
    }

    private class ButtonTouchListener implements Button.OnTouchListener {
        String dataToSend = "";

        public boolean onTouch(View view, MotionEvent event) {
            switch (view.getId()) {
                case R.id.btn_up:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        dataToSend = Constants.HEIGHT_CONTROL + Constants.HT_TOP;
                        mListener.sendData(dataToSend);
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        view.performClick();
                        dataToSend = Constants.HEIGHT_CONTROL + Constants.HT_UP;
                        mListener.sendData(dataToSend);
                    }
                    break;
                case R.id.btn_down:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        dataToSend = Constants.HEIGHT_CONTROL + Constants.HT_BOTTOM;
                        mListener.sendData(dataToSend);
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        view.performClick();
                        dataToSend = Constants.HEIGHT_CONTROL + Constants.HT_DOWN;
                        mListener.sendData(dataToSend);
                    }
                    break;
            }
            Log.d("DATATOSEND (CLICK)", dataToSend);
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
        void sendData(String dataToSend);
    }
}
