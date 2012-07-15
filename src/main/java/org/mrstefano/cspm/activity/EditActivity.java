package org.mrstefano.cspm.activity;

import org.mrstefano.cspm.CSPMAppWidgetProvider;
import org.mrstefano.cspm.R;
import org.mrstefano.cspm.manager.DataManager;
import org.mrstefano.cspm.manager.SoundProfileAudioManager;
import org.mrstefano.cspm.model.IconListItem;
import org.mrstefano.cspm.model.ProfileValidator;
import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.model.StreamSettings;
import org.mrstefano.cspm.view.EditStreamSettingsView;
import org.mrstefano.cspm.view.adapter.IconListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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

	public static final String KEY_PROFILE_INDEX = "profile_index";
	public static final String KEY_PROFILE = "profile";
	private static final int ACTIVITY_OPEN_RINGTONE_PICKER = 1;

	private static final IconListItem ICON_LIST_ITEM_MUTE = new IconListItem(R.drawable.silent);
	private static final IconListItem ICON_LIST_ITEM_VIBRATE = new IconListItem(R.drawable.vibrate);
	private static final IconListItem ICON_LIST_ITEM_LOW = new IconListItem(R.drawable.low);
	private static final IconListItem ICON_LIST_ITEM_NORMAL = new IconListItem(R.drawable.normal);
	private static final IconListItem ICON_LIST_ITEM_LOUD = new IconListItem(R.drawable.loud);

	private static final IconListItem[] ICON_LIST_ITEMS = { ICON_LIST_ITEM_MUTE, ICON_LIST_ITEM_VIBRATE, ICON_LIST_ITEM_LOW, ICON_LIST_ITEM_NORMAL, ICON_LIST_ITEM_LOUD };

	private static final int APPLY_CURRENT_SYSTEM_PROFILE_ID = Menu.FIRST;
	protected static final int DIALOG_ICON_ID = 0;

	private SparseArray<EditStreamSettingsView> streamSettingViews;
	
	private Integer index;
	private SoundProfile profile;
	private DataManager dataManager;
	private SoundProfileAudioManager audioManager;
	private EditText nameText;
	private ImageView iconView;
	private CheckBox hapticFeedbackEnabledCheckBox;
	private String iconName;
	private ProfileValidator profileValidator;
	private Integer currentStreamType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		audioManager = new SoundProfileAudioManager(this);
		dataManager = DataManager.getInstance(this);
		profileValidator = new ProfileValidator(this);
		
		setContentView(R.layout.profile_edit);
		setTitle(R.string.edit_title);

		nameText = (EditText) findViewById(R.id.profile_edit_name_edit_text);
		hapticFeedbackEnabledCheckBox = (CheckBox) findViewById(R.id.profile_edit_haptic_feedback_cb);
		
		initStreamControls();

		initButtons();

		loadState(savedInstanceState);
		
		populateFields();
	}

	private void initButtons() {
		iconView = (ImageView) findViewById(R.id.profile_edit_icon_iv);
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
			        if (index == null) {
						index = dataManager.addProfile(profile);
			        } else {
			            dataManager.updateProfile(profile, index);
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
				openRingtonePicker(StreamSettings.RINGER);
			}
		});
		
		Button deleteButton = (Button) findViewById(R.id.delete);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (index != null) {
					dataManager.deleteProfile(index);
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
		index = null;
		profile = null;
		if ( bundle == null ) {
			bundle = getIntent().getExtras();
		}
		if ( bundle != null ) {
			index = bundle.getInt(EditActivity.KEY_PROFILE_INDEX, -1);
			if ( index == -1 ) {
				index = null;
			}
			profile = (SoundProfile) bundle.getSerializable(EditActivity.KEY_PROFILE);
			if ( index != null && profile == null ) {
				profile = dataManager.loadProfile(index);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == ACTIVITY_OPEN_RINGTONE_PICKER) {
          Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
          String ringtoneUri = uri != null ? uri.toString(): null;
          StreamSettings streamSettings = this.profile.getStreamSettings(currentStreamType);
          streamSettings.ringtoneUri = ringtoneUri;
          EditStreamSettingsView editStreamSettingsView = streamSettingViews.get(currentStreamType);
          editStreamSettingsView.apply(streamSettings);
	  }
	}
	
	private void applyCurrentSystemProfile() {
		SoundProfile currentProfile = extractProfileFromView();
		SoundProfile systemProfile = audioManager.extractProfileFromCurrentSystemSettings();
		if ( currentProfile != null ) {
			systemProfile.name = currentProfile.name;
		}
		profile = systemProfile;
		populateFields();
	}

	private void initStreamControls() {
		streamSettingViews = new SparseArray<EditStreamSettingsView>();
		LinearLayout streamControlsContainer = (LinearLayout) findViewById(R.id.stream_controls_container);
		final int N = StreamSettings.STREAM_TYPES.length;
		for (int index = 0; index < N; index ++) {
			final int streamType = StreamSettings.STREAM_TYPES[index];
			EditStreamSettingsView streamSettingsView = new EditStreamSettingsView(this, streamType);
			streamSettingsView.selectRingtoneButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					openRingtonePicker(streamType);
				}
			});
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
        	hapticFeedbackEnabledCheckBox.setChecked(profile.haptickFeedbackEnabled);
        	
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
       	outState.putInt(EditActivity.KEY_PROFILE_INDEX, index);
       	outState.putSerializable(EditActivity.KEY_PROFILE, currentProfile);
    }
	
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
        CSPMAppWidgetProvider.updateAllWidgets(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
	public void openRingtonePicker(int streamType) {
		Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		String title = getString(R.string.edit_select_ringtone_popup_title);
		i.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, title );
		i.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
		int ringtoneType;
		switch(streamType) {
		case StreamSettings.RINGER:
			ringtoneType = RingtoneManager.TYPE_RINGTONE;
			break;
		case StreamSettings.NOTIFICATION:
			ringtoneType = RingtoneManager.TYPE_NOTIFICATION;
			break;
		case StreamSettings.ALARM:
			ringtoneType = RingtoneManager.TYPE_ALARM;
			break;
		default:
			ringtoneType = RingtoneManager.TYPE_ALL;
		}
		i.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, ringtoneType);
		currentStreamType = streamType;
		this.startActivityForResult(i, ACTIVITY_OPEN_RINGTONE_PICKER);
	}
	

    private void saveState() {
    	SoundProfile currentProfile = extractProfileFromView();
    	Intent intent = getIntent();
    	if ( index != null ) {
    		intent.putExtra(KEY_PROFILE_INDEX, index);
    	}
    	intent.putExtra(KEY_PROFILE, currentProfile);
    }
    
	private SoundProfile extractProfileFromView() {
        SoundProfile profile = new SoundProfile();
        String name = nameText.getText().toString();
        profile.name = name;
		profile.icon = iconName;
		profile.haptickFeedbackEnabled = hapticFeedbackEnabledCheckBox.isChecked();
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
