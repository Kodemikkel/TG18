package eu.dromnes.tg18.tg18;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

abstract class DataFormatter {
    private static final String TAG = "DataFormatter";

    // Reads a data code and returns a map containing each of the codes
    static Map<String, String> readDataAsMap(String dataToRead) {
        Map<String, String> dataCodes = new HashMap<>();
        if(dataToRead.length() == 11) {
            String prefix = dataToRead.substring(0, Constants.INTERNAL.length());
            String systemCode = dataToRead.substring(Constants.INTERNAL.length(), Constants.INTERNAL.length()+2);
            String toSystemCode = dataToRead.substring(Constants.INTERNAL.length()+2, Constants.INTERNAL.length()+4);
            String resultCode = dataToRead.substring(Constants.INTERNAL.length()+4, Constants.INTERNAL.length()+6);
            String actionCode = dataToRead.substring(Constants.INTERNAL.length()+6, Constants.INTERNAL.length()+8);

            dataCodes.clear();

            dataCodes.put("Prefix", prefix);
            dataCodes.put("System", systemCode);
            dataCodes.put("FromSystem", toSystemCode);
            dataCodes.put("Result", resultCode);
            dataCodes.put("Action", actionCode);
        } else {
            Log.e(TAG, "Invalid data code (must be a correctly formatted hex string)");
            dataCodes.clear();
            dataCodes.put("Valid", "false");
        }
        return dataCodes;
    }

    static String[] readData(String dataToRead) {
        String[] dataCodes;
        if(dataToRead.length() == 11) {
            dataCodes = new String[5];
            String prefix = dataToRead.substring(0, Constants.INTERNAL.length());
            String systemCode = dataToRead.substring(Constants.INTERNAL.length(), Constants.INTERNAL.length()+2);
            String toSystemCode = dataToRead.substring(Constants.INTERNAL.length()+2, Constants.INTERNAL.length()+4);
            String resultCode = dataToRead.substring(Constants.INTERNAL.length()+4, Constants.INTERNAL.length()+6);
            String actionCode = dataToRead.substring(Constants.INTERNAL.length()+6, Constants.INTERNAL.length()+8);

            dataCodes[0] = prefix;
            dataCodes[1] = systemCode;
            dataCodes[2] = toSystemCode;
            dataCodes[3] = resultCode;
            dataCodes[4] = actionCode;
        } else {
            Log.e(TAG, "Invalid data code (must be a correctly formatted hex string)");
            dataCodes = new String[1];
            dataCodes[0] = "false";
        }
        return dataCodes;
    }
}
