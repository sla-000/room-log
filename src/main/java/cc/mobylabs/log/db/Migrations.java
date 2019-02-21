package cc.mobylabs.log.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

/**
 * Migrations
 */
class Migrations {
	/**
	 * 1 to 2
	 */
//	static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//		@Override
//		public void migrate(final SupportSQLiteDatabase database) {
//			database.execSQL( "ALTER TABLE Log ADD COLUMN app_name TEXT;" );
//			database.execSQL( "DROP INDEX IF EXISTS `index_Log_unixtime_ms_verbosity_tag_sent`;" );
//			database.execSQL( "CREATE INDEX `index_Log_unixtime_ms_verbosity_tag_sent` ON `Log` (`unixtime_ms`, `app_name`, `verbosity`, `tag`, `sent`);" );
//		}
//	};

	/**
	 * 1 to 3
	 */
	static final Migration MIGRATION_1_3 = new Migration(1, 3) {
		@Override
		public void migrate(final SupportSQLiteDatabase database) {
			database.execSQL( "ALTER TABLE Log ADD COLUMN name TEXT;" );
			database.execSQL( "DROP INDEX IF EXISTS 'index_Log_id_sent';" );
			database.execSQL( "CREATE INDEX 'index_Log_id_millis_name_sent' ON 'Log' ('id', 'millis', 'name', 'sent');" );
		}
	};
}
