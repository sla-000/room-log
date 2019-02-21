package cc.mobylabs.log.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;

/**
 * Created by sla on 27/12/17.
 *
 * Log model
 *
 * Version 1:
 * "CREATE TABLE `Log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `millis` INTEGER, `vvv` TEXT, `tag` TEXT, `text` TEXT, `sent` INTEGER)"
 * "CREATE INDEX `index_Log_id_sent` ON `Log` (`id`, `sent`)"
 */
@SuppressWarnings( "WeakerAccess" )
@Entity( tableName = "Log",
	indices = { @Index(value = {"id", "millis", "name", "sent"}) } )
public class LogModel {
	@PrimaryKey(autoGenerate = true)
	@Nullable
	private Long id;

	/**
	 * Unixtime, ms
	 */
	@ColumnInfo( name = "millis" )
	@Nullable
	private Long millis;

	/**
	 * Package name
	 */
	@ColumnInfo( name = "name" )
	@Nullable
	private String name;

	/**
	 * Verbosity type [V|D|I|W|E]
	 */
	@ColumnInfo( name = "vvv" )
	@Nullable
	private String vvv;

	/**
	 * Log tag
	 */
	@ColumnInfo( name = "tag" )
	@Nullable
	private String tag;

	/**
	 * Log text value
	 */
	@ColumnInfo( name = "text" )
	@Nullable
	private String text;

	/**
	 * 1 - Is sent to server, 0 - awaiting to be send to servet
	 */
	@ColumnInfo( name = "sent" )
	@Nullable
	private Boolean sent;

	@Ignore
	public LogModel( final Long millis, @NonNull final String vvv, @NonNull final String tag, @NonNull final String text) {
		this( millis, "", vvv, tag, text );
	}

	/**
	 * Ctor
	 */
	public LogModel( final Long millis, @NonNull final String name, @NonNull final String vvv, @NonNull final String tag, @NonNull final String text) {
		setId( null );
		setSent( false );

		setMillis( millis );
		setName( name );
		setVvv( vvv );
		setTag( tag );
		setText( text );
	}

	@Nullable
	public Long getId() {
		return id;
	}
	public void setId( @Nullable final Long id ) {
		this.id = id;
	}

	@NonNull
	public Long getMillis() {
		if( millis == null ) {
			millis = 0L;
		}
		return millis;
	}
	public void setMillis( @Nullable final Long millis ) {
		this.millis = millis;
	}

	@NonNull
	public String getName() {
		if( name == null ) {
			name = "";
		}
		return name;
	}
	public void setName( @Nullable final String name ) {
		this.name = name;
	}

	@NonNull
	public String getVvv() {
		if( vvv == null ) {
			vvv = "";
		}
		return vvv;
	}
	public void setVvv( @Nullable final String verbosity ) {
		if (verbosity != null && verbosity.length() > 1 ) {
			this.vvv = verbosity.substring( 0, 1 );
		}
		else {
			this.vvv = verbosity;
		}
	}

	@NonNull
	public String getTag() {
		if( tag == null ) {
			tag = "";
		}
		return tag;
	}

	public void setTag( @Nullable final String tag ) {
		if (tag != null && tag.length() > MAX_TAG_LEN ) {
			this.tag = tag.substring( 0, MAX_TAG_LEN );
		}
		else {
			this.tag = tag;
		}
	}

	@NonNull
	public String getText() {
		if( text == null ) {
			text = "";
		}
		return text;
	}
	public void setText( @Nullable final String text ) {
		if (text != null && text.length() > MAX_TEXT_LEN ) {
			this.text = text.substring( 0, MAX_TEXT_LEN );
		}
		else {
			this.text = text;
		}
	}

	@NonNull
	public Boolean isSent() {
		if( sent == null ) {
			sent = false;
		}
		return sent;
	}
	public void setSent( @Nullable final Boolean sent ) {
		this.sent = sent;
	}

	@Ignore
	@NonNull
	public String toJson() {
		final JsonObject json = new JsonObject();

		json.addProperty( "millis", millis );
		json.addProperty( "name", name );
		json.addProperty( "vvv", vvv );
		json.addProperty( "tag", tag );
		json.addProperty( "text", text );

		return json.toString();
	}

	@Ignore
	@Override
	@NonNull
	public String toString() {
		final StringBuilder sb = new StringBuilder( "LogModel{" );
		sb.append( "id=" ).append( id );
		sb.append( ", millis=" ).append( millis );
		sb.append( ", name='" ).append( name ).append( '\'' );
		sb.append( ", vvv='" ).append( vvv ).append( '\'' );
		sb.append( ", tag='" ).append( tag ).append( '\'' );
		sb.append( ", text='" ).append( text ).append( '\'' );
		sb.append( ", sent=" ).append( sent );
		sb.append( '}' );
		return sb.toString();
	}

	@Ignore
	private final static int MAX_TEXT_LEN = 2048;
	@Ignore
	private final static int MAX_TAG_LEN = 32;
}
