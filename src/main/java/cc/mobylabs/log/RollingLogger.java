package cc.mobylabs.log;

import android.support.annotation.NonNull;

import net.jcip.annotations.ThreadSafe;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * log to rolling files
 */
@ThreadSafe
public class RollingLogger {
	/**
	 * Construct a RollingLogger
	 *
	 * @param dir          Log file directory
	 * @param name         Log file name
	 * @param fileMaxSize  Individual log file's maximum size (in bytes)
	 * @param maxfileCount Maximum log file count
	 */
	public RollingLogger( @NonNull final String dir, @NonNull final String name, long fileMaxSize, int maxfileCount ) {
		if( fileMaxSize <= 8_192 ) {
			fileMaxSize = 8_192;
			System.err.println( "Warning: Corrected fileMaxSize to MIN value=" + fileMaxSize );
		}

		if( maxfileCount <= 1 ) {
			maxfileCount = 1;
			System.err.println( "Warning: Corrected maxfileCount to MIN value=" + maxfileCount );
		}

		mDir = dir;
		mName = name;
		mFileMaxSize = fileMaxSize;
		mMaxFileCount = maxfileCount;

		final File dirFile = new File( mDir );

		if( !dirFile.exists() ) {
			if( !dirFile.mkdirs() ) {
				System.err.println( "Error: Can't create dirs=" + mDir );
			}
		}

		mFile = new File( mDir, mName );

		new Thread( () -> {
			while( ! Thread.currentThread().isInterrupted() ) {
				try {
					final String log = mLogsQueue.take();

					writeToFile( log );
				}
				catch( InterruptedException e ) {
					System.err.println( "RollingLogThread: Interrupted" );
					Thread.currentThread().interrupt();
				}
			}

			System.err.println( "RollingLogThread: Exit" );
		}, "RollingLogThread" ).start();
	}

	/**
	 * Write a log. (Synchronized)
	 *
	 * @param log The log content
	 */
	public synchronized void write( @NonNull final String log ) {
		writeToQueue( log );
	}

	/**
	 * Write a log in a line. (Synchronized)
	 *
	 * @param log The log content
	 */
	public synchronized void writeLn( @NonNull final String log ) {
		writeToQueue( log + LINE_SEPARATOR );
	}

	private void writeToQueue( @NonNull final String log ) {
		if( ! mLogsQueue.offer( log ) ) {
			System.err.println( "writeToQueue: mLogsQueue is full, ignore new log=" + log );
		}
	}

	private void writeLogToFile( @NonNull final String log, final boolean append ) {
		try( BufferedOutputStream fos = new BufferedOutputStream( new FileOutputStream( mFile, append ) ) ) {
			fos.write( log.getBytes() );

			fos.flush();
		}
		catch( final FileNotFoundException e ) {
			System.err.println( "Error: Can't write str=" + log + ", FileNotFoundException=" + e );

			if( !mFile.getParentFile().mkdirs() ) {
				System.err.println( "Error: Can't create dirs=" + mDir );
			}

			mFile = new File( mDir, mName );
		}
		catch( final IOException e ) {
			System.err.println( "Error: Can't write str=" + log + ", IOException=" + e );

			mFile = new File( mDir, mName );
		}
	}

	/**
	 * Get next file name
	 *
	 * @return Next file name
	 */
	private String getNextName() {
		final int index = mNextFileIndex;

		nextIndex();

		return mName + COUNT_SEPARATOR + index;
	}

	/**
	 * Calc next index
	 */
	private void nextIndex() {
		final int index = mNextFileIndex + 1;

		if( index >= mMaxFileCount ) {
			mNextFileIndex = 0;
		}
		else {
			mNextFileIndex = index;
		}
	}

	/**
	 * Write a log
	 *
	 * @param log The log content
	 */
	private void writeToFile( @NonNull final String log ) {
		if( mFile.length() < mFileMaxSize ) {
			writeLogToFile( log, true );
		}
		else {
			final File newFile = new File( mDir, getNextName() );

			if( !mFile.renameTo( newFile ) ) {
				System.err.println( "Error: Can't rename mFile=" + mFile + " to newFile=" + newFile );
			}

			mFile = new File( mDir, mName );

			writeLogToFile( log, false );
		}
	}

	/**
	 * Separator between the log file name and its index
	 */
	private static final String COUNT_SEPARATOR = "~";
	/**
	 * Line separator
	 */
	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	/**
	 * Log file directory
	 */
	private final String mDir;
	/**
	 * Log file name
	 */
	private final String mName;
	/**
	 * Individual log file's maximum size (in bytes)
	 */
	private final long mFileMaxSize;
	/**
	 * Maximum log file count
	 */
	private final int mMaxFileCount;
	/**
	 * Current editing file
	 */
	private File mFile;

	/**
	 * Next file index
	 */
	private int mNextFileIndex;

	/**
	 * Queue for logs
	 */
	@NonNull
	private final BlockingQueue<String> mLogsQueue = new ArrayBlockingQueue<>( 1000 );
}
