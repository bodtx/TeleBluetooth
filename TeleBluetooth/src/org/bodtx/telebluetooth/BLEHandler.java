package org.bodtx.telebluetooth;

import java.lang.ref.WeakReference;

import org.bodtx.telebluetooth.MainActivity.BLeThread;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class BLEHandler extends Handler {
	private final WeakReference<MainActivity> mTarget;

	public BLEHandler(MainActivity context) {
		mTarget = new WeakReference<MainActivity>((MainActivity) context);
	}

	@Override
	public void handleMessage(android.os.Message msg) {
		mTarget.get().textView.setText(msg.getData().getString(
				MainActivity.statusMsg));
		ImageView image = mTarget.get().image;
		if (msg.getData().containsKey("image")) {
			if (msg.getData().getInt("image") == 1) {
				image.setImageResource(R.drawable.on);
			} else if (msg.getData().getInt("image") == 0) {
				image.setImageResource(R.drawable.off);
			} else if (msg.getData().getInt("image") == -1) {
				image.setImageResource(R.drawable.ko);
				image.setTag(R.drawable.ko);
			}
		}

		if (msg.getData().getBoolean("done")) {
			mTarget.get().messageBundle.remove("done");
			mTarget.get().beforeFinish();
		}

	};
}
