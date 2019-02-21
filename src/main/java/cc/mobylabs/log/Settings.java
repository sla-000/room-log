package cc.mobylabs.log;

import android.os.Environment;

import java.io.File;

/**
 *
 * Do not forget add permissions to manifest:
 *
 * <pre>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * </pre>
 */
public class Settings {

	/**
	 * Rolling log file path
	 */
	public static final String LOG_DIR = "mobylabs/log/";

	/**
	 * Rolling log file path
	 */
	public static final String LOG_PATH = Utils.isJUnitTest ? LOG_DIR : Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + LOG_DIR;

	/**
	 * Rolling log file initial name
	 */
	public static final String LOG_NAME = "serviceapp.log";

	/**
	 * Rolling log file maximum size, bytes
	 */
	public static final int LOG_MAX_BYTES = 1_000_000;

	/**
	 * Rolling log files maximum number
	 */
	public static final int LOG_MAX_NUM = 10;

}
