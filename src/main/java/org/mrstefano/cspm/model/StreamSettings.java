package org.mrstefano.cspm.model;

import java.io.Serializable;

import android.media.AudioManager;
import android.net.Uri;

public class StreamSettings implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int RINGER = AudioManager.STREAM_RING;
	public static final int NOTIFICATION = AudioManager.STREAM_NOTIFICATION;
	public static final int ALARM = AudioManager.STREAM_ALARM;
	public static final int SYSTEM = AudioManager.STREAM_SYSTEM;
	public static final int MUSIC = AudioManager.STREAM_MUSIC;
	public static final int VOICE_CALL = AudioManager.STREAM_VOICE_CALL;
	public static final int[] STREAM_TYPES = {RINGER, NOTIFICATION, ALARM, SYSTEM, MUSIC};
	
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
