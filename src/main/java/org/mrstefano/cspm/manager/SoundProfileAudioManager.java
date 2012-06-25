package org.mrstefano.cspm.manager;

import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.model.StreamSettings;

import android.content.Context;
import android.media.AudioManager;

public class SoundProfileAudioManager {

	private AudioManager audioManager;
	//private Vibrator vibratorManager;

	public SoundProfileAudioManager(Context context) {
		super();
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		//vibratorManager = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void applyProfile(SoundProfile profile) {
		int ringerMode = profile.getRingerMode();
		audioManager.setRingerMode(ringerMode);
    	for (int streamType : StreamSettings.STREAM_TYPES) {
			StreamSettings streamSettings = profile.getStreamSettings(streamType);
			if ( streamSettings != null ) {
				applyStreamSetting(streamType, streamSettings, 0);
			}
		}
    }
	
	public SoundProfile extractSystemProfile() {
		SoundProfile profile = new SoundProfile();
		for (int streamType : StreamSettings.STREAM_TYPES) {
			int maxVol = audioManager.getStreamMaxVolume(streamType);
			int vol = audioManager.getStreamVolume(streamType);
			int volPercent = ( (Double) (Math.floor(vol * 100 / maxVol)) ).intValue();
			int vibrateSettings;
			switch ( streamType ) {
			case StreamSettings.RINGER:
				vibrateSettings = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
				break;
			case StreamSettings.NOTIFICATION:
				vibrateSettings = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION);
				break;
			default:
				vibrateSettings = AudioManager.VIBRATE_SETTING_OFF;
			}
			boolean vibrate = vibrateSettings == AudioManager.VIBRATE_SETTING_ON;
			StreamSettings streamSettings = new StreamSettings(volPercent, vibrate);
			profile.putStreamSetting(streamType, streamSettings);
		}
		return profile;
	}
	
	private void applyStreamSetting(int streamType, StreamSettings streamSettings, int flags) {
		int maxVolume = audioManager.getStreamMaxVolume(streamType);
		double vol = Math.ceil(maxVolume * streamSettings.volume / 100);
		audioManager.setStreamVolume(streamType, Double.valueOf(vol).intValue(), flags);
		switch ( streamType ) {
		case StreamSettings.RINGER:
			toggleVibrate(AudioManager.VIBRATE_TYPE_RINGER, streamSettings.vibrate);
			break;
		case StreamSettings.NOTIFICATION:
			toggleVibrate(AudioManager.VIBRATE_TYPE_NOTIFICATION, streamSettings.vibrate);
			break;
		}
	}
    
    private void toggleVibrate(int vibrateType, boolean on) {
    	audioManager.setVibrateSetting(vibrateType, on ? AudioManager.VIBRATE_SETTING_ON: AudioManager.VIBRATE_SETTING_OFF);
    }
}
