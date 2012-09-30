package com.samen.assets;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samen.assets.AssetsCopyTask.CopyTaskProgressListener;

public class MainActivity extends Activity implements CopyTaskProgressListener {
	private TextView mSdcardStatusTextView, mFilenameTextView;
	private ProgressBar mProgressBar;

	private boolean isCopyingInProgress = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSdcardStatusTextView = (TextView) findViewById(R.id.textView2);
		mFilenameTextView = (TextView) findViewById(R.id.textView4);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressBar.setMax(100);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_copy) {
			new AssetsCopyTask(this, this).execute("");
			isCopyingInProgress = true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSdcardStatusTextView.setText(isSDCardPresent() ? R.string.mounted
				: R.string.unmounted);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_copy).setEnabled(!isCopyingInProgress);
		return super.onPrepareOptionsMenu(menu);
	}

	public static boolean isSDCardPresent() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public void setCurrentFileName(String filename) {
		if (filename != null && !TextUtils.isEmpty(filename))
			mFilenameTextView.setText(filename);
	}

	@Override
	public void setCurrentProgress(int progress) {
		if(progress>=0 && progress<=mProgressBar.getMax())
			mProgressBar.setProgress(progress);
	}

	@Override
	public void copyResult(boolean success) {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(700);
		isCopyingInProgress = false;
		mFilenameTextView.setText(R.string.space);
	}

}
