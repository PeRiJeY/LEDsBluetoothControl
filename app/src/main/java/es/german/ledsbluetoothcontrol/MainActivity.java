package es.german.ledsbluetoothcontrol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.thebluealliance.spectrum.SpectrumPalette;

import es.german.ledsbluetoothcontrol.bluetooth.BluetoothManager;
import es.german.ledsbluetoothcontrol.listener.SeekBarListener;
import es.german.ledsbluetoothcontrol.message.BtModes;
import es.german.ledsbluetoothcontrol.state.GlobalState;

public class MainActivity extends AppCompatActivity {

    private enum MODES {OFF, ON, CYCLE, FADE};
    private MODES actualMode = MODES.OFF;

    private Spinner modesSpinner;

    BluetoothManager btManager;

    ProgressDialog progressDialog;

    TextView appStatus;

    private SeekBar sliderGroupBrightnessSlider;
    private TextView sliderGroupBrightnessValue;

    private SeekBar sliderGroupSpeedSlider;
    private TextView sliderGroupSpeedValue;

    private SpectrumPalette colorPickerView;

    private int selectedColor = 0xff556677;

    // Layouts
    LinearLayout colorPickerLayout;
    LinearLayout brightnessLayout;
    LinearLayout speedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appStatus = (TextView) findViewById(R.id.appStatus);

        initSeekBar();
        initModeSpinner();
        initColorPicker();
        initLayout();

        btManager = new BluetoothManager(this) {
            @Override
            public void onConnect(boolean status) {
                onConnectEvent(status);
            }
        };
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btManager.disconnectBT();
    }

    private void initColorPicker() {
        /*colorPickerView = (ColorPickerView) findViewById(R.id.colorPickerView);

        colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColor = color;

                updateState();
            }
        });*/

        colorPickerView = (SpectrumPalette) findViewById(R.id.colorPickerView);

        colorPickerView.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                selectedColor = color;

                updateState();
            }
        });
    }

    private void initSeekBar() {
        sliderGroupSpeedSlider = (SeekBar) findViewById(R.id.sliderGroupSpeedSlider);
        sliderGroupSpeedValue = (TextView) findViewById(R.id.sliderGroupSpeedValue);

        SeekBarListener listenerSpeed = new SeekBarListener(sliderGroupSpeedValue, this);
        sliderGroupSpeedSlider.setOnSeekBarChangeListener(listenerSpeed);

        sliderGroupBrightnessSlider = (SeekBar) findViewById(R.id.sliderGroupBrightnessSlider);
        sliderGroupBrightnessValue = (TextView) findViewById(R.id.sliderGroupBrightnessValue);

        SeekBarListener listenerValue = new SeekBarListener(sliderGroupBrightnessValue, this);
        sliderGroupBrightnessSlider.setOnSeekBarChangeListener(listenerValue);
    }

    private void initLayout() {
        colorPickerLayout = (LinearLayout) findViewById(R.id.colorPickerLayout);
        brightnessLayout = (LinearLayout) findViewById(R.id.brightnessLayout);
        speedLayout = (LinearLayout) findViewById(R.id.speedLayout);

        // hideAllLayouts();
        speedLayout.setVisibility(View.GONE);
    }
    private void hideAllLayouts() {
        colorPickerLayout.setVisibility(View.GONE);
        brightnessLayout.setVisibility(View.GONE);
        speedLayout.setVisibility(View.GONE);
    }

    private void initModeSpinner() {
        modesSpinner = (Spinner) findViewById(R.id.modesSpinner);
        modesSpinner.setSelection(1);

        modesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if ("CYCLE".equals(selected)) {
                    actualMode = MODES.CYCLE;
                    GlobalState.btMessage.setMode(BtModes.ALA_CYCLECOLORS);
                } else if ("FADE".equals(selected)) {
                    actualMode = MODES.FADE;
                    GlobalState.btMessage.setMode(BtModes.ALA_FADECOLORSLOOP);
                } else if ("ON".equals(selected)) {
                    actualMode = MODES.ON;
                    GlobalState.btMessage.setMode(BtModes.ALA_ON);
                } else {
                    actualMode = MODES.OFF;
                    GlobalState.btMessage.setMode(BtModes.ALA_OFF);
                }
                updateLayouts();
                updateState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothManager.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    progressDialog = ProgressDialog.show(this, getResources().getString(R.string.pleaseWait), getResources().getString(R.string.makingConnectionString), true);
                    String deviceAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    btManager.connectBT(deviceAddress);

                }else {
                    // Failure retrieving MAC address
                    Toast.makeText(this, R.string.macFailed, Toast.LENGTH_SHORT).show();
                }
                break;
            case BluetoothManager.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled
                    btManager.bindBT();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public void onClickConnect(View view) {
        appStatus.setText(R.string.statusConnecting);
        if(btManager.enableBT()) {
            btManager.bindBT();
        }
    }

    public void onConnectEvent(boolean status) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (status) {
            appStatus.setText(R.string.statusConnected);
        } else {
            appStatus.setText(R.string.statusError);
        }
    }

    public void updateState() {
        updateMessageValues();
        sendMessageBT();
    }

    public void updateMessageValues() {
        GlobalState.btMessage.getColor().setRed(Color.red(selectedColor));
        GlobalState.btMessage.getColor().setGreen(Color.green(selectedColor));
        GlobalState.btMessage.getColor().setBlue(Color.blue(selectedColor));

        GlobalState.btMessage.getBrightness().setRed(sliderGroupBrightnessSlider.getProgress());
        GlobalState.btMessage.getBrightness().setGreen(sliderGroupBrightnessSlider.getProgress());
        GlobalState.btMessage.getBrightness().setBlue(sliderGroupBrightnessSlider.getProgress());

        GlobalState.btMessage.setSpeed(sliderGroupSpeedSlider.getProgress());
    }

    public void sendMessageBT() {
        if (btManager.isBtConnected()) {
            updateMessageValues();
            String message = GlobalState.btMessage.message() + "\n";
            btManager.write(message);
        } else {
            Toast.makeText(this, R.string.no_bt_device, Toast.LENGTH_SHORT).show();
        }

    }


    private void updateLayouts() {
        hideAllLayouts();
        switch (actualMode) {
            case ON:
                colorPickerLayout.setVisibility(View.VISIBLE);
                brightnessLayout.setVisibility(View.VISIBLE);
                break;
            case CYCLE:
                brightnessLayout.setVisibility(View.VISIBLE);
                speedLayout.setVisibility(View.VISIBLE);
                break;
            case FADE:
                brightnessLayout.setVisibility(View.VISIBLE);
                speedLayout.setVisibility(View.VISIBLE);
                break;
        }
    }


}
