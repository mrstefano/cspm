package org.mrstefano.cspm.view;

import org.mrstefano.cspm.model.StreamSettings;
import org.mrstefano.cspm.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditStreamSettingsView extends LinearLayout {
	
	public TextView titleTextView;
	public SeekBar volumeSeekBar;
	public CheckBox vibrateCheckBox;

	public EditStreamSettingsView(Context context) {
		super(context);
		init();
	}

	public EditStreamSettingsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.profile_edit_stream_settings,this, true);
		titleTextView = (TextView) findViewById(R.id.settings_title_text);
		volumeSeekBar = (SeekBar) findViewById(R.id.volume_sb);
		vibrateCheckBox = (CheckBox) findViewById(R.id.vibrate_cb);
	}
	
	public void apply(StreamSettings streamSettings) {
		volumeSeekBar.setProgress(streamSettings.volume);
		if ( vibrateCheckBox != null ) {
			vibrateCheckBox.setChecked(streamSettings.vibrate);
		}
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
