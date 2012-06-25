package org.mrstefano.cspm.activity;

import org.mrstefano.cspm.manager.DataManager;
import org.mrstefano.cspm.manager.SoundProfileAudioManager;
import org.mrstefano.cspm.model.IconListItem;
import org.mrstefano.cspm.model.ProfileValidator;
import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.model.StreamSettings;
import org.mrstefano.cspm.view.EditStreamSettingsView;
import org.mrstefano.cspm.view.adapter.IconListAdapter;
import org.mrstefano.cspm.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class EditActivity extends Activity {

	public static final String KEY_POSITION = "position";
	public static final String KEY_SOUND_PROFILE = "sound_profile";

	private static final IconListItem ICON_LIST_ITEM_MUTE = new IconListItem(R.drawable.silent);
	private static final IconListItem ICON_LIST_ITEM_VIBRATE = new IconListItem(R.drawable.vibrate);
	private static final IconListItem ICON_LIST_ITEM_LOW = new IconListItem(R.drawable.low);
	private static final IconListItem ICON_LIST_ITEM_NORMAL = new IconListItem(R.drawable.normal);
	private static final IconListItem ICON_LIST_ITEM_LOUD = new IconListItem(R.drawable.loud);

	private static final IconListItem[] ICON_LIST_ITEMS = { ICON_LIST_ITEM_MUTE, ICON_LIST_ITEM_VIBRATE, ICON_LIST_ITEM_LOW, ICON_LIST_ITEM_NORMAL, ICON_LIST_ITEM_LOUD };

	private static final int APPLY_CURRENT_SYSTEM_PROFILE_ID = Menu.FIRST;
	protected static final int DIALOG_ICON_ID = 0;

	private SparseArray<EditStreamSettingsView> streamSettingViews;
	
	private Integer position;
	private SoundProfile profile;
	private DataManager dataManager;
	private SoundProfileAudioManager audioManager;
	private EditText nameText;
	private ImageView iconView;
	private String iconName;
	private ProfileValidator profileValidator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		audioManager = new SoundProfileAudioManager(this);
		dataManager = DataManager.getInstance(this);
		profileValidator = new ProfileValidator(this);
		
		setContentView(R.layout.profile_edit);
		setTitle(R.string.edit_title);

		nameText = (EditText) findViewById(R.id.name);
		
		initStreamControls();

		initButtons();

		loadState(savedInstanceState);
		
		populateFields();
	}

	private void initButtons() {
		iconView = (ImageView) findViewById(R.id.iconView);
		iconView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showDialog(DIALOG_ICON_ID);
			}
		});
		
		Button saveButton = (Button) findViewById(R.id.save);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				trimProfileName();
				SoundProfile profile = extractProfileFromView();
				if ( profileValidator.validate(profile) ) {
			        if (position == null) {
						position = dataManager.addProfile(profile);
			        } else {
			            dataManager.updateProfile(profile, position);
			        }
					setResult(RESULT_OK);
					finish();
				}
			}
		});
		
		Button resetButton = (Button) findViewById(R.id.reset);
		resetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				profile = new SoundProfile();
				profile.name = nameText.getText().toString();
				populateFields();
			}
		});
		
		Button deleteButton = (Button) findViewById(R.id.delete);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (position != null) {
					dataManager.deleteProfile(position);
				}
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	
	protected void trimProfileName() {
		String name = nameText.getText().toString();
		name = name.trim();
		nameText.setText(name);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
	    switch(id) {
	    case DIALOG_ICON_ID:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setTitle(getString(R.string.edit_dialog_icon_title));
	    	IconListAdapter adapter = new IconListAdapter(this, R.layout.icon_list_row, ICON_LIST_ITEMS);
	    	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
    			@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    			IconListItem item = ICON_LIST_ITEMS[which];
	    			iconView.setImageResource(item.iconId);
	    			iconName = IconListAdapter.getIconName(item.iconId);
	    			//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	    	    }
	    	});
	    	dialog = builder.create();
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}

	private void loadState(Bundle bundle) {
		position = null;
		profile = null;
		if ( bundle == null ) {
			bundle = getIntent().getExtras();
		}
		if ( bundle != null ) {
			position = bundle.getInt(EditActivity.KEY_POSITION, -1);
			if ( position == -1 ) {
				position = null;
			}
			profile = (SoundProfile) bundle.getSerializable(EditActivity.KEY_SOUND_PROFILE);
			if ( position != null && profile == null ) {
				profile = dataManager.loadProfile(position);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, APPLY_CURRENT_SYSTEM_PROFILE_ID, 0, R.string.edit_menu_apply_current_sound_settings);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case APPLY_CURRENT_SYSTEM_PROFILE_ID:
			applyCurrentSystemProfile();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void applyCurrentSystemProfile() {
		SoundProfile currentProfile = extractProfileFromView();
		SoundProfile systemProfile = audioManager.extractSystemProfile();
		if ( currentProfile != null ) {
			systemProfile.name = currentProfile.name;
		}
		profile = systemProfile;
		populateFields();
	}

	private void initStreamControls() {
		streamSettingViews = new SparseArray<EditStreamSettingsView>();
		LinearLayout streamControlsContainer = (LinearLayout) findViewById(R.id.stream_controls_container);
		for (int streamType : StreamSettings.STREAM_TYPES) {
			int titleResId;
			boolean vibrateCheckBoxVisible = false;
			switch (streamType) {
			case StreamSettings.RINGER:
				titleResId = R.string.edit_ringer;
				vibrateCheckBoxVisible = true;
				break;
			case StreamSettings.NOTIFICATION:
				titleResId = R.string.edit_notification;
				vibrateCheckBoxVisible = true;
				break;
			case StreamSettings.ALARM:
				titleResId = R.string.edit_alarm;
				break;
			case StreamSettings.MUSIC:
				titleResId = R.string.edit_music;
				break;
			case StreamSettings.SYSTEM:
				titleResId = R.string.edit_system;
				break;
			case StreamSettings.VOICE_CALL:
				titleResId = R.string.edit_voice_call;
				break;
			default:
				continue;
			}
			EditStreamSettingsView streamSettingsView = new EditStreamSettingsView(this);
			streamSettingsView.titleTextView.setText(titleResId);
			streamSettingsView.vibrateCheckBox.setVisibility(vibrateCheckBoxVisible ? CheckBox.VISIBLE: CheckBox.INVISIBLE);
			streamSettingViews.put(streamType, streamSettingsView);
			streamControlsContainer.addView(streamSettingsView);
		}
	}

	private void populateFields() {
		if ( profile != null ) {
	        nameText.setText(profile.name);
	        int iconId = -1;
	        iconName = profile.icon;
	        iconId = IconListAdapter.getIconId(iconName);
        	iconView.setImageResource(iconId);
	        for (int streamType : StreamSettings.STREAM_TYPES) {
				EditStreamSettingsView streamSettingsView = streamSettingViews.get(streamType);
				if ( streamSettingsView != null ) {
					StreamSettings streamSettings = profile.getStreamSettings(streamType);
					if ( streamSettings != null ) {
						streamSettingsView.apply(streamSettings);
					} else {
						streamSettingsView.reset();
					}
				}
	        }
		}
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        SoundProfile currentProfile = extractProfileFromView();
       	outState.putInt(EditActivity.KEY_POSITION, position);
       	outState.putSerializable(EditActivity.KEY_SOUND_PROFILE, currentProfile);
    }
	
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
    	SoundProfile currentProfile = extractProfileFromView();
    	Intent intent = getIntent();
    	if ( position != null ) {
    		intent.putExtra(KEY_POSITION, position);
    	}
    	intent.putExtra(KEY_SOUND_PROFILE, currentProfile);
    }
    
	private SoundProfile extractProfileFromView() {
        SoundProfile profile = new SoundProfile();
        String name = nameText.getText().toString();
        profile.name = name;
		profile.icon = iconName;
        for (int streamType : StreamSettings.STREAM_TYPES) {
			EditStreamSettingsView streamSettingView = streamSettingViews.get(streamType);
			if ( streamSettingView != null ) {
				StreamSettings settings = streamSettingView.extractSettings();
				profile.putStreamSetting(streamType, settings);
			}
        }
		return profile;
	}
}
