package org.mrstefano.cspm.view;

import org.mrstefano.cspm.R;
import org.mrstefano.cspm.model.StreamSettings;
import org.mrstefano.cspm.model.StreamSettings.Type;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditStreamSettingsView extends LinearLayout {
	
	public TextView titleTextView;
	public SeekBar volumeSeekBar;
	public CheckBox vibrateCheckBox;
	public Button selectRingtoneButton;
	private Type streamType;

	public EditStreamSettingsView(Context context) {
		super(context);
		init();
	}

	public EditStreamSettingsView(Context context, Type streamType) {
		super(context);
		this.streamType = streamType;
		init();
	}
	
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.profile_edit_stream_settings,this, true);
		titleTextView = (TextView) findViewById(R.id.settings_title_text);
		volumeSeekBar = (SeekBar) findViewById(R.id.volume_sb);
		vibrateCheckBox = (CheckBox) findViewById(R.id.vibrate_cb);
		selectRingtoneButton = (Button) findViewById(R.id.select_ringtone_btn);
		
		int titleResId;
		boolean vibrateCheckBoxVisible = false, selectableRingtone = false;
		switch (streamType) {
		case RINGER:
			titleResId = R.string.edit_ringer;
			vibrateCheckBoxVisible = true;
			selectableRingtone = true;
			break;
		case NOTIFICATION:
			titleResId = R.string.edit_notification;
			vibrateCheckBoxVisible = true;
			selectableRingtone = true;
			break;
		case ALARM:
			titleResId = R.string.edit_alarm;
			selectableRingtone = true;
			break;
		case MUSIC:
			titleResId = R.string.edit_music;
			break;
		case SYSTEM:
			titleResId = R.string.edit_system;
			break;
		case VOICE_CALL:
			titleResId = R.string.edit_voice_call;
			break;
		default:
			return;
		}
		titleTextView.setText(titleResId);
		if ( vibrateCheckBoxVisible ) { 
			vibrateCheckBox.setVisibility(View.VISIBLE);
			vibrateCheckBox.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		} else {
			vibrateCheckBox.setVisibility(View.INVISIBLE);
			vibrateCheckBox.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
		}
		selectRingtoneButton.setVisibility(selectableRingtone ? View.VISIBLE: View.INVISIBLE);
	}
	
	public void apply(StreamSettings streamSettings) {
		volumeSeekBar.setProgress(streamSettings.volume);
		if ( vibrateCheckBox != null ) {
			vibrateCheckBox.setChecked(streamSettings.vibrate);
		}
		String ringtoneTitle = getRingtoneTitle(streamSettings.ringtoneUri);
		if (ringtoneTitle == null ) {
			//TODO
			ringtoneTitle = getContext().getString(R.string.edit_no_ringtone_selected);
		}
		selectRingtoneButton.setText(ringtoneTitle);
	}

	private String getRingtoneTitle(String ringtoneUriStr) {
		if ( ringtoneUriStr != null ) {
			Uri uri = Uri.parse(ringtoneUriStr);
			Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
			if ( ringtone != null ) {
				String title = ringtone.getTitle(getContext());
				return title;
			}
		}
		return null;
	}
	
	public void reset() {
		volumeSeekBar.setProgress(0);
		if ( vibrateCheckBox != null ) {
			vibrateCheckBox.setChecked(false);
		}
	}
	
	public StreamSettings extractSettings() {
		StreamSettings settings = new StreamSettings();
		settings.volume = volumeSeekBar.getProgress();
		if ( vibrateCheckBox != null ) {
			settings.vibrate = vibrateCheckBox.isChecked();
		}
		return settings;
	}

}
