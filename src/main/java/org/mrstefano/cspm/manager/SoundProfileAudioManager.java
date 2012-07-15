package org.mrstefano.cspm.manager;

import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.model.StreamSettings;
import org.mrstefano.cspm.model.StreamSettings.Type;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;

public class SoundProfileAudioManager {

	private AudioManager audioManager;
	private Context context;

	public SoundProfileAudioManager(Context context) {
		super();
		this.context = context;
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	public void applyProfile(SoundProfile profile) {
		int ringerMode = profile.getRingerMode();
		audioManager.setRingerMode(ringerMode);
		Type[] types = Type.values();
		for (Type streamType : types) {
			StreamSettings streamSettings = profile.getStreamSettings(streamType);
			if ( streamSettings != null ) {
				applyStreamSetting(streamType, streamSettings, 0);
			}
		}
    	System.putInt(context.getContentResolver(), System.HAPTIC_FEEDBACK_ENABLED, profile.haptickFeedbackEnabled ? 1: 0);
    }
	
	public SoundProfile extractProfileFromCurrentSystemSettings() {
		SoundProfile profile = new SoundProfile();
		Type[] types = StreamSettings.Type.values();
		for (Type streamType : types) {
			int audioManagerStreamType = getAudioManagerStreamType(streamType);
			int maxVol = audioManager.getStreamMaxVolume(audioManagerStreamType);
			int vol = audioManager.getStreamVolume(audioManagerStreamType);
			int volPercent = ( (Double) (Math.floor(vol * 100 / maxVol)) ).intValue();
			int vibrateSettings;
			Uri ringtoneUri;
			switch ( streamType ) {
			case RINGER:
				vibrateSettings = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
				break;
			case NOTIFICATION:
				vibrateSettings = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION);
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
				break;
			case ALARM:
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
				vibrateSettings = AudioManager.VIBRATE_SETTING_OFF;
				break;
			default:
				ringtoneUri = null;
				vibrateSettings = AudioManager.VIBRATE_SETTING_OFF;
			}
			boolean vibrate = vibrateSettings == AudioManager.VIBRATE_SETTING_ON;
			StreamSettings streamSettings = new StreamSettings(volPercent, vibrate, ringtoneUri);
			profile.putStreamSetting(streamType, streamSettings);
		}
		try {
			int hapticFeedbackEnabledInt = System.getInt(context.getContentResolver(), System.HAPTIC_FEEDBACK_ENABLED);
			profile.haptickFeedbackEnabled = hapticFeedbackEnabledInt == 1;
		} catch (SettingNotFoundException e) {}
		
		return profile;
	}
	
	private void applyStreamSetting(Type streamType, StreamSettings streamSettings, int flags) {
		int audioManagerStreamType = getAudioManagerStreamType(streamType);
		int maxVolume = audioManager.getStreamMaxVolume(audioManagerStreamType);
		double vol = Math.ceil(maxVolume * streamSettings.volume / 100);
		audioManager.setStreamVolume(audioManagerStreamType, Double.valueOf(vol).intValue(), flags);
		Uri ringtoneUri = parseUri(streamSettings.ringtoneUri);
		switch ( streamType ) {
		case RINGER:
			toggleVibrate(AudioManager.VIBRATE_TYPE_RINGER, streamSettings.vibrate);
			applyRingtone(RingtoneManager.TYPE_RINGTONE, ringtoneUri);
			break;
		case NOTIFICATION:
			toggleVibrate(AudioManager.VIBRATE_TYPE_NOTIFICATION, streamSettings.vibrate);
			applyRingtone(RingtoneManager.TYPE_NOTIFICATION, ringtoneUri);
			break;
		case ALARM:
			applyRingtone(RingtoneManager.TYPE_ALARM, ringtoneUri);
			break;
		}
	}
	
	protected int getAudioManagerStreamType(Type streamType) {
		switch(streamType) {
		case RINGER:
			return AudioManager.STREAM_RING;
		case NOTIFICATION:
			return AudioManager.STREAM_NOTIFICATION;
		case ALARM:
			return AudioManager.STREAM_ALARM;
		case MUSIC:
			return AudioManager.STREAM_MUSIC;
		case SYSTEM:
			return AudioManager.STREAM_SYSTEM;
		case VOICE_CALL:
			return AudioManager.STREAM_VOICE_CALL;
		default:
			return -1;
		}
	}
    
    private void applyRingtone(int type, Uri uri) {
		if ( uri != null ) {
			RingtoneManager.setActualDefaultRingtoneUri(context, type, uri);
		}
	}

	private void toggleVibrate(int vibrateType, boolean on) {
    	audioManager.setVibrateSetting(vibrateType, on ? AudioManager.VIBRATE_SETTING_ON: AudioManager.VIBRATE_SETTING_OFF);
    }
    
    private Uri parseUri(String uri) {
		Uri parsedUri;
		if ( uri != null ) {
			parsedUri = Uri.parse(uri);
		} else {
			parsedUri = null;
		}
		return parsedUri;
    }
}
