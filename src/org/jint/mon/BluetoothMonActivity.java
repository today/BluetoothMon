package org.jint.mon;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.jint.mon.device.Device;
import org.jint.util.LogUtil;

public class BluetoothMonActivity extends Activity {
	/** Called when the activity is first created. */
	private Button clearButton = null;
	private Button findButton = null;
	private TextView bluetoothFilterTextView = null;
	private TextView bluetoothList = null;

	private static final int SEARCH_DURATION = 1000;
	private Timer bluetoothDiscoveryTimer;
	private BluetoothDiscoveryReciver bluetoothDiscoveryReciver;
	private Handler handler;
	private BluetoothAdapter bluetoothAdapter;
	private ArrayList<Device> devices;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		clearButton = (Button) findViewById(R.id.clear);
		findButton = (Button) findViewById(R.id.find);
		bluetoothFilterTextView = (TextView) findViewById(R.id.bluetoothFilter);
		bluetoothList = (TextView) findViewById(R.id.bluetoothList);

		clearButton.setOnClickListener(new clearButtonListener());
		findButton.setOnClickListener(new findButtonListener());

		handler = new Handler();
		devices = new ArrayList<Device>();

		// 注册蓝牙扫描监听
		bluetoothDiscoveryReciver = new BluetoothDiscoveryReciver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(bluetoothDiscoveryReciver, filter);

		System.out.println("onCreat is OK");
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 检测蓝牙是否启用，如果没有启用，则先开启蓝牙
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}

	}

	@Override
	protected void onDestroy() {
		// 取消注册蓝牙监听。
		stopSearching();
		unregisterReceiver(bluetoothDiscoveryReciver);

		super.onDestroy();
	}

	class clearButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			bluetoothList.setText("");
		}
	}

	class findButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			System.out.println("findButtonListener is start");

			bluetoothDiscoveryTimer = new Timer();
			System.out.println("Timer is OK");

			bluetoothDiscoveryTimer.schedule(new BluetoothDiscoveryTimerTask(),
					SEARCH_DURATION);
			System.out.println("Timer run is OK");

			if (bluetoothDiscoveryTimer != null) {
				bluetoothDiscoveryTimer.cancel();
			}

			startSearching();

		}
	}

	class BluetoothDiscoveryTimerTask extends TimerTask {
		@Override
		public void run() {
			// 如果没有搜索到蓝牙设备，重新搜索
			System.out.println("Discovery finish, devices count="
					+ devices.size());

			handler.post(new Runnable() {

				public void run() {
					System.out.println("BluetoothDiscoveryTimerTask is run");
					// startSearching();
					
					// print bluetooth info to textbox
					for (int i = 0; i < devices.size(); i++) {
						bluetoothList.append("\n--------------------");
						bluetoothList.append(devices.get(i).toString());
					}

					// clear list
					devices.clear();

				}
			});
		}
	}

	class BluetoothDiscoveryReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				BluetoothDevice bluetoothDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
						Short.MIN_VALUE);

				Device device = new Device();
				device.setRssi(rssi);
				device.setBluetoothDevice(bluetoothDevice);

				// LogUtil.debug("Discovery device " + device);

				devices.add(device);
			}
		}
	}

	protected void startSearching() {
		LogUtil.debug("Discovery start");

		// 取消原来的搜索，然后重新开始搜索
		bluetoothAdapter.cancelDiscovery();
		bluetoothAdapter.startDiscovery();

		// 计时器在蓝牙搜索超时后，结束蓝牙搜索
		if (bluetoothDiscoveryTimer != null) {
			bluetoothDiscoveryTimer.cancel();
		}
		bluetoothDiscoveryTimer = new Timer();
		bluetoothDiscoveryTimer.schedule(new BluetoothDiscoveryTimerTask(),
				SEARCH_DURATION);

		

	}

	protected void stopSearching() {
		bluetoothAdapter.cancelDiscovery();
		if (bluetoothDiscoveryTimer != null) {
			bluetoothDiscoveryTimer.cancel();
		}
	}

}