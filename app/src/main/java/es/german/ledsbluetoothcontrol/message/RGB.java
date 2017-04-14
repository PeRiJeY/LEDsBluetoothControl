package es.german.ledsbluetoothcontrol.message;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JuanGerman on 06/12/2016.
 */

public class RGB {

    @SerializedName("r")
    private int red;

    @SerializedName("g")
    private int green;

    @SerializedName("b")
    private int blue;

    public RGB() { }

    public RGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }
}
