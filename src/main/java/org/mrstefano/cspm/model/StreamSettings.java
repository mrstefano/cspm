package org.mrstefano.cspm.model;

import java.io.Serializable;

import android.net.Uri;

public class StreamSettings implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		RINGER(1), NOTIFICATION(2), ALARM(3), SYSTEM(4), MUSIC(5), VOICE_CALL(6);
		
		private int code;

		private Type(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}

		public static Type valueOf(int code) {
			Type[] values = values();
			for (Type type : values) {
				if ( type.getCode() == code ) {
					return type;
				}
			}
			return null;
		}
		
	}

	public int volume;
	public boolean vibrate;
	public String ringtoneUri;
	
	public StreamSettings() {
	}

	public StreamSettings(int volume) {
		this.volume = volume;
	}

	public StreamSettings(int volume, boolean vibrate) {
		this(volume);
		this.vibrate = vibrate;
	}

	public StreamSettings(int volume, boolean vibrate, String ringtoneUri) {
		this(volume, vibrate);
		this.vibrate = vibrate;
		this.ringtoneUri = ringtoneUri;
	}

	public StreamSettings(int volume, boolean vibrate, Uri ringtoneUri) {
		this(volume, vibrate, ringtoneUri != null ? ringtoneUri.toString(): null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ringtoneUri == null) ? 0 : ringtoneUri.hashCode());
		result = prime * result + (vibrate ? 1231 : 1237);
		result = prime * result + volume;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StreamSettings other = (StreamSettings) obj;
		if (ringtoneUri == null) {
			if (other.ringtoneUri != null)
				return false;
		} else if (!ringtoneUri.equals(other.ringtoneUri))
			return false;
		if (vibrate != other.vibrate)
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}

	
}
