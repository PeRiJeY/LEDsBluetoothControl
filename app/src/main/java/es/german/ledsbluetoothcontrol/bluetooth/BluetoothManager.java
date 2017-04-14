package es.german.ledsbluetoothcontrol.bluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import es.german.ledsbluetoothcontrol.DeviceListActivity;
import es.german.ledsbluetoothcontrol.R;

/**
 * Created by JuanGerman on 07/12/2016.
 */

public abstract class BluetoothManager {

    //---REQUEST CODES
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    //---BLUETOOTH VARS AND STUFF
    private boolean btConnected = false;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private ConnectThread mConnectThread = null;
    private String deviceAddress = null;
    // Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if not in use);
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Activity associated
    private Activity activity;

    public BluetoothManager(Activity activity) {
        this.activity = activity;
    }

    /*
    * Return true if yet enable in method call moment
    * */
    public boolean enableBT() {
        boolean result = false;
        //---CHECK IF BT ADAPTER EXISTS
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(activity, R.string.no_bt_device, Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }

        //---IF BT NOT ON, ENABLE
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            result = true;
        }
        return result;

    }

    public void bindBT() {
        // Launch the DeviceListActivity to see devices and do scan
        Intent serverIntent = new Intent(activity, DeviceListActivity.class);
        activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void connectBT(String deviceAddress) {
        // Connect to device with specified MAC address
        mConnectThread = new ConnectThread(deviceAddress);
        mConnectThread.start();
        // activity.runOnUiThread(mConnectThread);
    }

    public void disconnectBT() {
        if (outStream != null ) {
            try {
                outStream.close();
            } catch (IOException e) { }
        }
        if (btSocket != null && btSocket.isConnected()) {
            try {
                btSocket.close();
            } catch (IOException e) { }
        }
        btConnected = false;
    }

    class ConnectThread extends Thread {
        private String address;
        private boolean connectionStatus;

        ConnectThread(String MACaddress) {
            address = MACaddress;
            connectionStatus = true;
        }

        public void run() {
            try {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                try {
                    btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                } catch (IOException e) {
                    connectionStatus = false;
                }
            }catch (IllegalArgumentException e) {
                connectionStatus = false;
            }
            mBluetoothAdapter.cancelDiscovery();
            try {
                btSocket.connect();
            } catch (IOException e1) {
                connectionStatus = false;
                try {
                    btSocket.close();
                } catch (IOException e2) {
                }
            }
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e2) {
                connectionStatus = false;
            }
            btConnected = connectionStatus;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnect(btConnected);
                }
            });
        }
    }

    public void write(byte[] data) {
        if (outStream != null) {
            try {
                outStream.write(data);
            } catch (IOException e) {
            }
        }
    }

    public void write(String data) {
        write(data.getBytes());
    }

    public abstract void onConnect(boolean status);

    public boolean isBtConnected() {
        return btConnected;
    }

}
