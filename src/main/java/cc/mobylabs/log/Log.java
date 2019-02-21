package cc.mobylabs.log;

import android.support.annotation.NonNull;

import cc.mobylabs.log.db.LogDbManager;
import cc.mobylabs.log.db.LogModel;

import static cc.mobylabs.log.Utils.getDate;

public final class Log {
	public final static int MAX_STACK_DEPTH = 8;

	/**
	 * @param level VERBOSE, DEBUG, INFO, WARN, ERROR
	 *              from android.util.Log
	 */
	public static boolean isLoggable( final String tag, final int level) {
        return Utils.isJUnitTest || android.util.Log.isLoggable(tag, level) || android.util.Log.isLoggable(tag, level) || android.util.Log.isLoggable(tag, level);
    }

	public static String stackTrace(@NonNull final Throwable e) {
		final StringBuilder sb = new StringBuilder();

		sb.append( e.getMessage() ).append( "\n" );

		int count = 0;

		for( final StackTraceElement stack : e.getStackTrace() ) {
			sb.append( "\t" );

			sb.append( stack.toString() ).append( "\n" );
			++count;

			if( count >= MAX_STACK_DEPTH ) break;
		}

		return sb.toString();
	}

	public static synchronized void x( final long millis, @NonNull final String name, @NonNull final String vvv,
	                                   @NonNull final String tag, @NonNull final String msg) {
		if( Utils.isJUnitTest ) {
			System.out.println(getDate(millis) + " " + vvv +"/" + name + ":" + tag + ": " + msg);
		}
		else{
			LogDbManager.getInstance().getDb().add( new LogModel( millis, name, vvv, tag, msg ) );
		}
	}

	public static synchronized void v( final String tag, final String msg) {
		final long ms = System.currentTimeMillis();

		if( Utils.isJUnitTest ) {
			System.out.println(getDate(ms) + " V/" + tag + ": " + msg);
		}
		else{
			android.util.Log.v(tag, msg);
			mRollingLogger.writeLn(getDate(ms) + " V/" + tag + " " + msg + ";");
			LogDbManager.getInstance().getDb().add( new LogModel( ms, "V", tag, msg ) );
		}
	}

	public static synchronized void d( final String tag, final String msg) {
		final long ms = System.currentTimeMillis();

		if( Utils.isJUnitTest ) {
			System.out.println(getDate(ms) + " D/" + tag + ": " + msg);
		}
		else{
			android.util.Log.d(tag, msg);
			mRollingLogger.writeLn(getDate(ms) + " D/" + tag + " " + msg + ";");
			LogDbManager.getInstance().getDb().add( new LogModel( ms, "D", tag, msg ) );
		}
	}

	public static synchronized void i( final String tag, final String msg ) {
		i( tag, msg, false );
	}

	public static synchronized void w( final String tag, final String msg ) {
		w( tag, msg, false );
	}

	public static synchronized void e( final String tag, final String msg ) {
		e( tag, msg, false );
	}

	public static synchronized void i( final String tag, final String msg, final boolean flush) {
		final long ms = System.currentTimeMillis();

		if( Utils.isJUnitTest ) {
			System.out.println(getDate(ms) + " I/" + tag + ": " + msg);
		}
		else{
			android.util.Log.i(tag, msg);
			mRollingLogger.writeLn(getDate(ms) + " I/" + tag + " " + msg + ";");
			LogDbManager.getInstance().getDb().add( new LogModel( ms, "I", tag, msg ) );
			if( flush ) {
				LogDbManager.getInstance().getDb().flush();
			}
		}
	}

	public static synchronized void w( final String tag, final String msg, final boolean flush) {
		final long ms = System.currentTimeMillis();

		if( Utils.isJUnitTest ) {
			System.out.println(getDate(ms) + " W/" + tag + ": " + msg);
		}
		else{
			android.util.Log.w(tag, msg);
			mRollingLogger.writeLn(getDate(ms) + " W/" + tag + " " + msg + ";");
			LogDbManager.getInstance().getDb().add( new LogModel( ms, "W", tag, msg ) );
			if( flush ) {
				LogDbManager.getInstance().getDb().flush();
			}
		}
    }

    public static synchronized void e( final String tag, final String msg, final boolean flush) {
	    final long ms = System.currentTimeMillis();

        if( Utils.isJUnitTest ) {
	        System.out.println(getDate(ms) + " E/" + tag + ": " + msg);
        }
        else{
            android.util.Log.e(tag, msg);
	        mRollingLogger.writeLn(getDate(ms) + " E/" + tag + " " + msg + ";");
            LogDbManager.getInstance().getDb().add( new LogModel( ms, "E", tag, msg ) );
	        if( flush ) {
		        LogDbManager.getInstance().getDb().flush();
	        }
        }
    }

	@NonNull
	private final static RollingLogger mRollingLogger = new RollingLogger(Settings.LOG_PATH, Settings.LOG_NAME,
			Settings.LOG_MAX_BYTES, Settings.LOG_MAX_NUM);
}
