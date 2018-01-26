package eu.dromnes.tg18.tg18;

public interface Constants {
    // TODO: CREATE A SYSTEM FOR FORMATTING AND READING MESSAGE_DATA CODES BOTH INTERNALLY AND EXTERNALLY
    // TODO: GET A SYSTEM FOR HANDLER CONSTANTS

    // HANDLER CONSTANTS //
    // Indicates status messages
    int MESSAGE_STATUS = 10;

    // Indicates a status message from Bluetooth
    int BLUETOOTH = 100;
    // Sub-codes for Bluetooth status messages
    int BT_OFF = 101;
    int BT_ON = 102;
    int BT_DEVICE_NAME = 103;
    int BT_TURNED_OFF = 104;
    int BT_TURNED_ON = 105;

    //Indicates messages about data
    int MESSAGE_DATA = 200;
    // Sub-codes for data messages
    int DATA_SEND = 201;
    int DATA_RCV = 202;

    // Indicates messages for toast
    int MESSAGE_TOAST = 300;
    // Sub-codes for toast messages
    int TOAST_SHORT = 301;
    int TOAST_LONG = 302;

    // The index for a toast message
    String TOAST = "toast";

    // The index for a device name message
    String DEVICE_NAME = "device_name";

    ///////////////////////

    // MESSAGE_DATA CODES //
    // Prefixes
    String INTERNAL = ";0_";
    String SYSTEM = ";1_";
    String LIGHT_CONTROL = ";2_";
    String HEIGHT_CONTROL = ";3_";
    String PC_CONTROL = ";4_";


    // SYS_BLUETOOTH action codes
    int REQUEST_BT_ON = 1;


    // Combine with prefixes to form a complete data code
    String BT_ENABLED = "01001100";
    String BT_DISABLED = "01000100";
    String BT_TURN_ON = "01000011";
    String BT_TURN_OFF = "01000001";


    // DATA CODES USED TO TRANSMIT DATA //
        // LIGHT CONTROL FUNCTION CODES //
        // Combine with prefix and a alphaVal value to form a complete data code
    String LT_DIMUP = "";
    String LT_DIMDN = "";
    String LT_OFF = "";
    String LT_ON = "";
    String LT_FLASH = "G00000";
    String LT_STROBE = "H00000";
    String LT_FADE = "I00000";
    String LT_SMOOTH = "J00000";


        // HEIGHT CONTROL CODES //
        // Combine with prefix to form a complete data code
    String HT_TOP = "G0000000";
    String HT_UP = "H0000000";
    String HT_STOP = "I0000000";
    String HT_DOWN = "J0000000";
    String HT_BOTTOM = "K0000000";


        // PC CONTROL CODES //
        // Combine with prefix to form a complete data code
    String PC_ONOFF = "G0000000";
    String PC_ONOFF_RELEASE = "H0000000";
    String PC_RESTART = "I0000000";
    String PC_RESTART_RELEASE = "J0000000";
    //////////////////////////////////////
}
