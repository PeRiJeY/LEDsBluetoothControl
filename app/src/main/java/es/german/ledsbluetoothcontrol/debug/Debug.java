package es.german.ledsbluetoothcontrol.debug;

import android.widget.TextView;

/**
 * Created by JuanGerman on 06/12/2016.
 */

public class Debug {
    private static TextView debugView;

    private Debug() {}

    public static void setDebugger (TextView debugView) {
        Debug.debugView = debugView;
    }

    public static void print(String text) {
        if (debugView != null) {
            debugView.setText(text);
        }
    }
}
