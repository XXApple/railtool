package com.commonrail.mtf.util.BlueToothUtils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.commonrail.mtf.mvp.ui.service.BluetoothLeService;
import com.commonrail.mtf.util.common.L;

/**
 * Created by wengyiming on 2016/3/29.
 */
public class BluetoothUtils {
    private final static String TAG = "BlueToothUtils";
    public static final int REQUEST_ENABLE_BT = 1;
    public static BluetoothAdapter mBluetoothAdapter;

    public static IntentFilter getGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public static BluetoothAdapter getBluetoothAdapter(Context ctx) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        return mBluetoothAdapter;
    }


    public static void openBluetooth(Activity ctx) {
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        //弹窗申请打开蓝牙
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = getBluetoothAdapter(ctx);
            if (mBluetoothAdapter == null) {
                L.e(TAG, "该设备不支持蓝牙");
                Toast.makeText(ctx, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (!mBluetoothAdapter.isEnabled()) {
            L.e(TAG, "蓝牙未打开,申请打开蓝牙");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ctx.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
