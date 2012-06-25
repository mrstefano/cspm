package org.mrstefano.cspm.model;

import org.mrstefano.cspm.R;
import org.mrstefano.cspm.view.adapter.IconListAdapter;

import android.content.Context;

public class SoundProfileBuilder {

	public static SoundProfile buildNormalProfile(Context context) {
		return buildProfile(context, 80, false, R.string.normal, IconListAdapter.ICON_NORMAL);
	}

	public static SoundProfile buildSilentProfile(Context context) {
		return buildProfile(context, 0, false, R.string.silent, IconListAdapter.ICON_SILENT);
	}

	public static SoundProfile buildVibrateProfile(Context context) {
		return buildProfile(context, 0, true, R.string.vibrate, IconListAdapter.ICON_VIBRATE);
	}
	
	public static SoundProfile buildLoudProfile(Context context) {
		return buildProfile(context, 100, true, R.string.loud, IconListAdapter.ICON_LOUD);
	}

	public static SoundProfile buildDefaultProfile(Context context) {
		SoundProfile profile = buildProfile(context, 80, false, null, IconListAdapter.ICON_DEFAULT);
		return profile;
	}
	
	private static SoundProfile buildProfile(Context context, int volume, boolean vibrate, Integer profileNameStringId, String icon) {
		SoundProfile profile = new SoundProfile();
		if ( profileNameStringId != null ) {
			profile.name = context.getString(profileNameStringId);
		}
		profile.icon = icon;
		StreamSettings streamSettings;
		for (Integer streamType : StreamSettings.STREAM_TYPES) {
			switch ( streamType ) {
			case StreamSettings.VOICE_CALL:
				streamSettings = new StreamSettings(Math.max(volume, 80), false);
				break;
			default:
				streamSettings = new StreamSettings(volume, vibrate);
			}
			profile.putStreamSetting(streamType, streamSettings);
		}
		return profile;
	}

}
