package org.mrstefano.cspm.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.media.AudioManager;

public class SoundProfile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int RINGER_MODE_SILENT = AudioManager.RINGER_MODE_SILENT;
	public static final int RINGER_MODE_VIBRATE = AudioManager.RINGER_MODE_VIBRATE;
	public static final int RINGER_MODE_NORMAL = AudioManager.RINGER_MODE_NORMAL;
	
	public String name;
	public String icon;
	private Map<Integer, StreamSettings> streamSettingsMap;

	public SoundProfile() {
		super();
		streamSettingsMap = new HashMap<Integer, StreamSettings>();
	}

	public void putStreamSetting(Integer streamType,
			StreamSettings streamSettings) {
		streamSettingsMap.put(streamType, streamSettings);
	}

	public int getRingerMode() {
		StreamSettings ringerSettings = getStreamSettings(StreamSettings.RINGER);
		StreamSettings notificationSettings = getStreamSettings(StreamSettings.NOTIFICATION);
		boolean vibrate = ringerSettings != null && ringerSettings.vibrate || notificationSettings != null && notificationSettings.vibrate;
		boolean silent = (ringerSettings == null || ringerSettings.volume == 0) && (notificationSettings == null || notificationSettings.volume == 0);
		int ringerMode;
		if ( vibrate && silent ) {
			ringerMode = RINGER_MODE_VIBRATE;
		} else if ( silent ) {
			ringerMode = RINGER_MODE_SILENT;
		} else {
			ringerMode = RINGER_MODE_NORMAL;
		}
		return ringerMode;
	}
	
	public StreamSettings getStreamSettings(Integer streamType) {
		return streamSettingsMap.get(streamType);
	}

	public Set<Integer> getStreamTypes() {
		return streamSettingsMap.keySet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((streamSettingsMap == null) ? 0 : streamSettingsMap
						.hashCode());
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
		SoundProfile other = (SoundProfile) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (streamSettingsMap == null) {
			if (other.streamSettingsMap != null)
				return false;
		} else if (!streamSettingsMap.equals(other.streamSettingsMap))
			return false;
		return true;
	}
	
}