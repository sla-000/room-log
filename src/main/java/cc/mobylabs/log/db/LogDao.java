package cc.mobylabs.log.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by sla on 27/12/17.
 *
 * Log model
 */
@Dao
interface LogDao {
	/**
	 * Get not sent yet records
	 *
	 * @param max_num Max number of records
	 * @return list of records
	 */
	@Query("SELECT * FROM Log WHERE sent=0 ORDER BY id ASC LIMIT :max_num")
	@NonNull
	List<LogModel> getNotSent( final int max_num );

	/**
	 * Insert records
	 *
	 * @param log Array of logs
	 */
	@Insert
	void insert( @NonNull LogModel... log );

	/**
	 * Delete old records
	 *
	 * @param sent 1-Only sent data, 0-Only not sent data
	 * @param millis Delete records older than this time in millis, inclusive
	 */
	@Query("DELETE FROM Log WHERE sent= :sent AND millis <= :millis")
	void deleteOld( final int sent, final long millis );

	/**
	 * Set records as sent
	 *
	 * @param startId Id of first sent log, inclusive
	 * @param endId Id of last sent log, inclusive
	 */
	@Query("UPDATE Log SET sent=1 WHERE sent=0 AND id >= :startId AND id <= :endId")
	void setAsSent( final long startId, final long endId );
}
