package org.jint.mon.device;

import android.bluetooth.BluetoothDevice;

public class Device {
	private short rssi;
	private BluetoothDevice bluetoothDevice;

	public String toString() {
		return "\n name:" + bluetoothDevice.getName() + " rssi:" + rssi 
				+ "\n mac:" + bluetoothDevice.getAddress();
	}

	public short getRssi() {
		return rssi;
	}

	public void setRssi(short rssi) {
		this.rssi = rssi;
	}

	public BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
		this.bluetoothDevice = bluetoothDevice;
	}

}
