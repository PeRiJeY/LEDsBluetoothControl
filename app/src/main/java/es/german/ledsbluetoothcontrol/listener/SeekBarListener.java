package es.german.ledsbluetoothcontrol.listener;

import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Observable;

import es.german.ledsbluetoothcontrol.MainActivity;

/**
 * Created by JuanGerman on 06/12/2016.
 */
public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

    private TextView bindedTextView = null;
    private MainActivity activity = null;

    public SeekBarListener(TextView bindedTextView, MainActivity activity) {
        this.bindedTextView = bindedTextView;
        this.activity = activity;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (bindedTextView != null) {
            bindedTextView.setText(String.valueOf(i));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        activity.updateState();
    }
}
