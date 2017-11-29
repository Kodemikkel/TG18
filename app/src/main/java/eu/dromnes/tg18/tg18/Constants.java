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

    //
    String TOAST = "toast";
    ///////////////////////

    // MESSAGE_DATA CODES //
    // Prefixes
    String INTERNAL = ";0_";
    String SYSTEM = ";1_";
    String LIGHT_CONTROL = ";2_";
    String HEIGHT_CONTROL = ";3_";
    String PC_CONTROL = ";4_";

    // COMMON CODES
    String NONE = "00";

    // System codes
    String SYS_BLUETOOTH = "01";

    // SYS_BLUETOOTH action codes
    int REQUEST_BT_OFF = 0;
    int REQUEST_BT_ON = 1;

    // SYS_BLUETOOTH action/result codes
    String SYS_BT_ON = "11";
    String SYS_BT_OFF = "01";
    ////////////////
}
