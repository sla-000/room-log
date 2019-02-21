package cc.mobylabs.log.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import cc.mobylabs.log.Log;

import static android.os.SystemClock.sleep;
import static android.util.Log.DEBUG;
import static android.util.Log.VERBOSE;

/**
 * Created by sla on 2018-09-11
 *
 * Log database
 */
@Database(entities = { LogModel.class}, //
		version = 3) //
public abstract class LogDb extends RoomDatabase {
	private final static String TAG = "LogDb";

	public abstract LogDao logDao();

	LogDb() {
		new Thread( () -> {
			while( ! Thread.currentThread().isInterrupted() ) {
				if( processAdd() ) {
					sleep( ADD_PERIOD_ms );
				}
				else {
					sleep( ADD_PERIOD_HURRY_ms );
				}
			}
		}, "LogDbThread").start();

	}

	/**
	 * Add log record
	 *
	 * @param log LogModel
	 * @return true - OK, false - queue is full
	 */
	public boolean add( @NonNull final LogModel log ) {
		if( addQueue.size() >= ADD_LIMIT ) {
			if(Log.isLoggable( TAG, DEBUG ) ) Log.d( TAG, "add: Queue is full, ignore log=" + log );

			return false;
		}

		addQueue.add( log );

		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "add: OK, log=" + log );

		return true;
	}

	/**
	 * Delete old records
	 */
	public void deleteOld() {
		final long currTime = System.currentTimeMillis();

		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "deleteOld: Start delete sent" );

		logDao().deleteOld( 1, currTime - TimeUnit.DAYS.toMillis( DELETE_SENT_d ) );

		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "deleteOld: Start delete not sent" );

		logDao().deleteOld( 0, currTime - TimeUnit.DAYS.toMillis( DELETE_NOT_SENT_d ) );

		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "deleteOld: End delete" );
	}

	/**
	 * Delete old records
	 */
	@NonNull
	public List<LogModel> getNotSent( final int limit ) {
		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "getNotSent: Start, limit=" + limit );

		final List<LogModel> list = logDao().getNotSent( limit );

		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "getNotSent: End, size=" + list.size() );

		return list;
	}

	/**
	 * Delete old records
	 */
	public void setAsSent( @NonNull final List<LogModel> list ) {
		final int size = list.size();

		if( size == 0 ) {
			if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "setAsSent: List size == 0" );
			return;
		}

		final Long start = list.get( 0 ).getId();
		final Long end = list.get( size - 1 ).getId();

		if( start == null || end == null ) {
			if(Log.isLoggable( TAG, DEBUG ) ) Log.d( TAG, "setAsSent: start=" + start + ", end=" + end );
		}
		else {
			if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "setAsSent: Start, size=" + size );

			logDao().setAsSent( start, end );

			if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "setAsSent: End" );
		}
	}

	/**
	 * Immediately write to db
	 */
	public void flush() {
		for( int q = 0; q < FLUSH_ATTEMPTS_MAX; ++q) {
			if( processAdd() ) {
				return;
			}
		}
	}

	/**
	 * Add records from addQueue in worker thread
	 *
	 * @return true - OK, false - have more records
	 */
	private synchronized boolean processAdd() {
		final List<LogModel> records = new ArrayList<>();

		for( int q = 0; q < TRANSACT_LIMIT; ++q) {
			final LogModel record = addQueue.poll();

			if( record == null ) {
				break;
			}

			records.add( record );
		}

		if( records.size() == 0 ) {
			if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "processAdd: records.size() == 0" );

			return true;
		}

		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "processAdd: Start add num=" + records.size() );

		logDao().insert( records.toArray( new LogModel[0] ) );

		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "processAdd: Finished add num=" + records.size() );

		if( records.size() >= TRANSACT_LIMIT ) {
			if(Log.isLoggable( TAG, DEBUG ) ) Log.d( TAG, "processAdd: There are more records" );

			return false;
		}

		if(Log.isLoggable( TAG, VERBOSE ) ) Log.d( TAG, "processAdd: Add records OK" );

		return true;
	}

	/**
	 * Queue of records
	 */
	@NonNull
	private final Queue<LogModel> addQueue = new ConcurrentLinkedQueue<>();

	private final static int DELETE_SENT_d = 7;
	private final static int DELETE_NOT_SENT_d = 31;

	private final static long ADD_PERIOD_ms = 17_000;
	private final static long ADD_PERIOD_HURRY_ms = 500;

	private final static int TRANSACT_LIMIT = 100;
	private final static int ADD_LIMIT = 500;

	private final static int FLUSH_ATTEMPTS_MAX = 10;
}
