package es.german.ledsbluetoothcontrol.state;

import es.german.ledsbluetoothcontrol.message.BtMessage;
import es.german.ledsbluetoothcontrol.message.BtModes;
import es.german.ledsbluetoothcontrol.message.RGB;

/**
 * Created by JuanGerman on 06/12/2016.
 */

public class GlobalState {
    public static BtMessage btMessage;

    static {
        RGB color = new RGB(100, 100, 100);
        RGB brightness = new RGB(0xAA, 0xAA, 0xAA);
        btMessage = new BtMessage(color, brightness, 1000, BtModes.ALA_OFF);
    }
}
