package cc.mobylabs.log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

	public static final boolean isJUnitTest = isJUnitTest();

	public static String getDate() {
		return Utils.dateToString( System.currentTimeMillis(), Utils.LOG_DATE_FMT );
	}

	public static String getDate( final long ms ) {
		return Utils.dateToString( ms, Utils.LOG_DATE_FMT );
	}

	/**
	 * Get date string
	 *
	 * @param timeInMillis date
	 * @param template string format
	 * @return date string
	 */
	public static String dateToString( final long timeInMillis, final String template) {
		return dateToString(new Date(timeInMillis), template);
	}

	public static String dateToString( final Date date, final String template) {
		final SimpleDateFormat dateFormatterSTR = new SimpleDateFormat(template, Locale.getDefault());
		return dateFormatterSTR.format(date);
	}

	public final static String LOG_DATE_FMT = "MM-dd/HH:mm:ss.SSS";

	private static boolean isJUnitTest() {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		final List<StackTraceElement> list = Arrays.asList(stackTrace);
		for ( final StackTraceElement element : list) {
			if (element.getClassName().startsWith("org.junit.")) {
				return true;
			}
		}
		return false;
	}
}
