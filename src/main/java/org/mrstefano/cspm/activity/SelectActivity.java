package org.mrstefano.cspm.activity;

import java.util.List;

import org.mrstefano.cspm.manager.DataManager;
import org.mrstefano.cspm.manager.SoundProfileAudioManager;
import org.mrstefano.cspm.model.IconListItem;
import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.model.SoundProfileBuilder;
import org.mrstefano.cspm.model.SoundProfilesData;
import org.mrstefano.cspm.view.adapter.IconListAdapter;
import org.mrstefano.cspm.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectActivity extends ListActivity {

	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;

	private static final int INSERT_ID = 1;
	private static final int RESET_DATA_ID = 2;

	private DataManager dataManager;
	private SoundProfileAudioManager soundProfileManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataManager = DataManager.getInstance(this);
		soundProfileManager = new SoundProfileAudioManager(this);
		
		setContentView(R.layout.profiles_list);
		ListView lv = getListView();
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
		    @Override
		    public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
		        editItem(pos);
		        return false;
		    }
		});
		populateView();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		dataManager.selectProfile(position);
		SoundProfile profile = dataManager.loadProfile(position);
		soundProfileManager.applyProfile(profile);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		menu.add(0, RESET_DATA_ID, 1, R.string.menu_reset_data);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case INSERT_ID:
			createProfile();
			return true;
		case RESET_DATA_ID:
			resetData();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private void createProfile() {
		Intent i = new Intent(this, EditActivity.class);
		SoundProfile profile = SoundProfileBuilder.buildDefaultProfile(this);
		i.putExtra(EditActivity.KEY_SOUND_PROFILE, profile);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	private void resetData() {
		dataManager.resetData();
		populateView();
	}

	private void editItem(int position) {
		Intent i = new Intent(this, EditActivity.class);
		SoundProfile profile = dataManager.loadProfile(position);
		i.putExtra(EditActivity.KEY_POSITION, position);
		i.putExtra(EditActivity.KEY_SOUND_PROFILE, profile);
		startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		populateView();
	}

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateView();
    }
    
    private void saveState() {
    	dataManager.saveData();
    }

    private void populateView() {
		SoundProfilesData data = dataManager.loadData();
		IconListItem[] profileIconItems = createProfileIconItems(data.getProfiles());
		IconListAdapter adapter = new IconListAdapter(this, android.R.layout.simple_list_item_single_choice, profileIconItems );
		
		setListAdapter(adapter);
		ListView listView = getListView();
		Integer selectedProfileIndex = data.getSelectedProfileIndex();
		if ( selectedProfileIndex != null ) {
			listView.setItemChecked(selectedProfileIndex, true);
			SoundProfile selectedProfile = data.getSelectedProfile();
			soundProfileManager.applyProfile(selectedProfile);
		}
	}

	private IconListItem[] createProfileIconItems(List<SoundProfile> profiles) {
		IconListItem[] result = new IconListItem[profiles.size()];
		for(int i = 0; i < profiles.size(); i++) {
			SoundProfile profile = profiles.get(i);
			Integer iconId = IconListAdapter.getIconId(profile.icon);
			IconListItem iconListItem = new IconListItem(iconId, profile.name);
			result[i] = iconListItem;
		}
		return result;
	}

}