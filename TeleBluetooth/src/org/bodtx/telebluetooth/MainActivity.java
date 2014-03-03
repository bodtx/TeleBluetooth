package org.bodtx.telebluetooth;

import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MyActivity";

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (state) {
				case BluetoothAdapter.STATE_ON:
					textView.append("Bluetooth activ�\n");
					startConnection();
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					textView.append("Activation Bluetooth ...\n");
					break;
				}
			}
		}
	};

	TextView textView;
	private BluetoothAdapter mBluetoothAdapter;

	private BluetoothSocket socket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.edit_message);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Register for broadcasts on BluetoothAdapter state change
		IntentFilter filter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(mReceiver, filter);

		if (mBluetoothAdapter == null) {
			textView.setText("bluetooth non support�");
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.enable();
			} else {
				startConnection();
			}
		}
	}

	private void startConnection() {
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		socket = null;
		BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice("20:13:10:15:38:91");
		textView.append("Accrochage distant\n");

		mBluetoothAdapter.cancelDiscovery();
		try {
			socket = device.createRfcommSocketToServiceRecord(UUID
					.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			socket.connect();
			textView.append("Connexion\n");

			socket.getOutputStream().write("state".getBytes());
			textView.append("Demande Etat\n");
			if (socket.getInputStream().available() > 0) {
				int state = socket.getInputStream().read();
				if (48 == state) {
					textView.append("Allumage\n");
					socket.getOutputStream().write("on".getBytes());
				} else {
					textView.append("Exctinction\n");
					socket.getOutputStream().write("off".getBytes());
				}
				image.setImageResource(R.drawable.ok);
			}
			
			


		} catch (IOException e) {
			Log.e(TAG, e.toString());
			image.setImageResource(R.drawable.ko);
		} finally {
			new CountDownTimer(5000, 1000) {

			     public void onTick(long millisUntilFinished) {
			    	 textView.append("Fermeture dans: " + millisUntilFinished / 1000+"\n");
			     }

			     public void onFinish() {
			    	 textView.append("Ciao!\n");
			    	 finish();
			     }
			  }.start();
		}

	}

	@Override
	protected void onDestroy() {
		try {
			socket.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		this.unregisterReceiver(mReceiver);
		textView.append("Fermeture Flux\n");
		mBluetoothAdapter.disable();
		textView.append("D�sactivation Bluetooth\n");
		
		super.onDestroy();
	}

}