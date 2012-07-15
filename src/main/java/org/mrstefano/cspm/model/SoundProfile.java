package org.mrstefano.cspm.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mrstefano.cspm.model.StreamSettings.Type;

import android.media.AudioManager;

public class SoundProfile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int RINGER_MODE_SILENT = AudioManager.RINGER_MODE_SILENT;
	public static final int RINGER_MODE_VIBRATE = AudioManager.RINGER_MODE_VIBRATE;
	public static final int RINGER_MODE_NORMAL = AudioManager.RINGER_MODE_NORMAL;
	
	public String name;
	public String icon;
	public boolean haptickFeedbackEnabled;
	private Map<Type, StreamSettings> streamSettingsMap;

	public SoundProfile() {
		super();
		haptickFeedbackEnabled = false;
		streamSettingsMap = new HashMap<Type, StreamSettings>();
	}

	public void putStreamSetting(Type streamType, StreamSettings streamSettings) {
		streamSettingsMap.put(streamType, streamSettings);
	}

	public int getRingerMode() {
		StreamSettings ringerSettings = getStreamSettings(Type.RINGER);
		StreamSettings notificationSettings = getStreamSettings(Type.NOTIFICATION);
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
	
	public StreamSettings getStreamSettings(Type streamType) {
		return streamSettingsMap.get(streamType);
	}

	public Set<Type> getStreamTypes() {
		return streamSettingsMap.keySet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (haptickFeedbackEnabled ? 1231 : 1237);
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
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
		if (haptickFeedbackEnabled != other.haptickFeedbackEnabled)
			return false;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
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
