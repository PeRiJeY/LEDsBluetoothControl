package es.german.ledsbluetoothcontrol.message;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import es.german.ledsbluetoothcontrol.debug.Debug;
import es.german.ledsbluetoothcontrol.state.GlobalState;

/**
 * Created by JuanGerman on 06/12/2016.
 */

public class BtMessage {

    @SerializedName("c")
    private RGB color;
    @SerializedName("b")
    private RGB brightness;
    @SerializedName("s")
    private int speed;
    @SerializedName("m")
    private int mode;

    public BtMessage() { }

    public BtMessage(RGB color, RGB brightness, int speed, int mode) {
        this.color = color;
        this.brightness = brightness;
        this.speed = speed;
        this.mode = mode;
    }

    public RGB getColor() {
        return color;
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public RGB getBrightness() {
        return brightness;
    }

    public void setBrightness(RGB brightness) {
        this.brightness = brightness;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String message() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(GlobalState.btMessage);
        return jsonInString;
    }
}
