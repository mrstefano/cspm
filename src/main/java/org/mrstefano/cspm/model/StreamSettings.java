package org.mrstefano.cspm.model;

import java.io.Serializable;

import android.media.AudioManager;

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
	
	public StreamSettings() {
		super();
	}

	public StreamSettings(int volume) {
		super();
		this.volume = volume;
	}

	public StreamSettings(int volume, boolean vibrate) {
		super();
		this.volume = volume;
		this.vibrate = vibrate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (vibrate != other.vibrate)
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}
	
}
