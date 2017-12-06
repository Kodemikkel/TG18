package eu.dromnes.tg18.tg18;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class StatusLog extends Fragment {

    private TextView statusLog;

    public StatusLog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_log, container, false);

        statusLog = view.findViewById(R.id.statusLog);
        appendTextFromFile(AppHandler.FILENAME);

        return view;
    }

    private void appendTextFromFile(String filepath) {
        try {
            FileInputStream inputStream = getContext().openFileInput(filepath);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            statusLog.append(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
