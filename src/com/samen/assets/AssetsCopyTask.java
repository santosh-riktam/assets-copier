package com.samen.assets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class AssetsCopyTask extends AsyncTask<Object, Object, Object> {
	private WeakReference<Context> mContextReference;
	private WeakReference<CopyTaskProgressListener> mCopyTaskProgressListenerReference;

	public AssetsCopyTask(Context mContext,
			CopyTaskProgressListener mCopyTaskProgressListener) {
		super();
		this.mContextReference = new WeakReference<Context>(mContext);
		this.mCopyTaskProgressListenerReference = new WeakReference<AssetsCopyTask.CopyTaskProgressListener>(
				mCopyTaskProgressListener);
	}

	@Override
	protected Object doInBackground(Object... params) {
		copyAssets();
		return true;
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
		if (mCopyTaskProgressListenerReference.get() != null) {
			mCopyTaskProgressListenerReference.get().setCurrentFileName(
					values[1].toString());
			mCopyTaskProgressListenerReference.get().setCurrentProgress(
					Integer.parseInt(values[0].toString()));
		}

	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		if (mCopyTaskProgressListenerReference.get() != null)
			mCopyTaskProgressListenerReference.get().copyResult(true);
	}

	public interface CopyTaskProgressListener {
		void setCurrentFileName(String filename);

		void setCurrentProgress(int progress);

		void copyResult(boolean success);

	}

	private void copyAssets() {
		if (mContextReference.get() == null)
			return;
		AssetManager assetManager = mContextReference.get().getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}
		for (int i = 1; i <= files.length; i++) {

			String filename = files[i - 1];
			InputStream in = null;
			OutputStream out = null;
			try {
				Thread.sleep(200);
				if (mContextReference.get() == null)
					return;
				int progress = (int) ((double) i * 100 / files.length);
				publishProgress(progress, filename);
				in = assetManager.open(filename);
				out = new FileOutputStream(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/tmp/" + filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (Exception e) {
				Log.e("tag", e.getMessage());
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}