package eu.dromnes.tg18.tg18;

public interface Constants {
    // TODO: CREATE A SYSTEM FOR FORMATTING AND READING DATA CODES BOTH INTERNALLY AND EXTERNALLY
    // TODO: GET A SYSTEM FOR HANDLER CONSTANTS

    // HANDLER CONSTANTS //
    // Message types
    int MESSAGE_READ = 0;
    int MESSAGE_WRITE = 1;
    int MESSAGE_TOAST = 2;
    int MESSAGE_STATE_CHANGE = 3;

    //
    String TOAST = "toast";
    ///////////////////////

    // Data prefixes
    String INTERNAL = ";0_";
    String SYSTEM = ";1_";
    String LIGHT_CONTROL = ";2_";
    String HEIGHT_CONTROL = ";3_";
    String PC_CONTROL = ";4_";

    // Bluetooth constants
    int REQUEST_BT_OFF = 0;
    int REQUEST_BT_ON = 1;

    // Data codes
    String BLUETOOTH = "01";
}
