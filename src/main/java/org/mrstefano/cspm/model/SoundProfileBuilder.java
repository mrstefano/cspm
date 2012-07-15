package org.mrstefano.cspm.model;

import org.mrstefano.cspm.R;
import org.mrstefano.cspm.view.adapter.IconListAdapter;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

public class SoundProfileBuilder {

	public static SoundProfile buildNormalProfile(Context context) {
		SoundProfile profile = buildProfile(context, 80, false, R.string.normal, IconListAdapter.ICON_NORMAL);
		profile.haptickFeedbackEnabled = true;
		return profile;
	}

	public static SoundProfile buildSilentProfile(Context context) {
		SoundProfile profile = buildProfile(context, 0, false, R.string.silent, IconListAdapter.ICON_SILENT);
		profile.haptickFeedbackEnabled = false;
		return profile;
	}

	public static SoundProfile buildVibrateProfile(Context context) {
		SoundProfile profile = buildProfile(context, 0, true, R.string.vibrate, IconListAdapter.ICON_VIBRATE);
		profile.haptickFeedbackEnabled = true;
		return profile;
	}
	
	public static SoundProfile buildLoudProfile(Context context) {
		SoundProfile profile = buildProfile(context, 100, true, R.string.loud, IconListAdapter.ICON_LOUD);
		profile.haptickFeedbackEnabled = true;
		return profile;
	}

	public static SoundProfile buildDefaultProfile(Context context) {
		SoundProfile profile = buildProfile(context, 80, false, null, IconListAdapter.ICON_DEFAULT);
		profile.haptickFeedbackEnabled = true;
		return profile;
	}
	
	private static SoundProfile buildProfile(Context context, int volume, boolean vibrate, Integer profileNameStringId, String icon) {
		SoundProfile profile = new SoundProfile();
		if ( profileNameStringId != null ) {
			profile.name = context.getString(profileNameStringId);
		}
		profile.icon = icon;
		StreamSettings streamSettings;
		Uri ringtoneUri = null;
		int finalVolume = volume;
		for (Integer streamType : StreamSettings.STREAM_TYPES) {
			switch ( streamType ) {
			case StreamSettings.RINGER:
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
				break;
			case StreamSettings.NOTIFICATION:
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
				break;
			case StreamSettings.ALARM:
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
				break;
			case StreamSettings.VOICE_CALL:
				finalVolume = 80;
				break;
			}
			streamSettings = new StreamSettings(finalVolume, vibrate, ringtoneUri);
			profile.putStreamSetting(streamType, streamSettings);
		}
		return profile;
	}

}
