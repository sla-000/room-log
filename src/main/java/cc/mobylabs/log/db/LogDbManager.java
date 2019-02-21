package cc.mobylabs.log.db;

import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cc.mobylabs.app.AppContext;

/**
 * Created by sla on 2018-09-11
 *
 * Log base manager
 */

public class LogDbManager {
	/**
	 * Get instance
	 */
	public static LogDbManager getInstance() {
		if( mLogDbManager == null ) {
			mLogDbManager = new LogDbManager();
		}

		return mLogDbManager;
	}

	/**
	 * Get log database
	 */
	@NonNull
	public LogDb getDb() {
		return mLogDb;
	}

	private LogDbManager() {
		mLogDb = Room.databaseBuilder( AppContext.getInstance().getOrThrow(),
				LogDb.class, "log.db")
//				.addMigrations( Migrations.MIGRATION_1_2, LogDb.MIGRATION_2_3) // migration sample
				.addMigrations( Migrations.MIGRATION_1_3)
				.build();
	}

	@NonNull
	private final LogDb mLogDb;

	@Nullable
	private static volatile LogDbManager mLogDbManager;
}
