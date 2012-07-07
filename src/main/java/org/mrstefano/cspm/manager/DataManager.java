package org.mrstefano.cspm.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mrstefano.cspm.manager.dataXml.SoundProfileDataSerializer;
import org.mrstefano.cspm.manager.dataXml.SoundProfilesXmlParser;
import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.model.SoundProfileBuilder;
import org.mrstefano.cspm.model.SoundProfilesData;

import android.content.Context;
import android.util.Log;

public class DataManager {

	private static final String FILE_NAME = "sound_profile_data_v1.xml";

	public static final String KEY_NAME = "name";

	private static DataManager instance;

	private SoundProfilesData data;
	
	private Context context;

	public static DataManager getInstance(Context context) {
		if ( instance == null ) {
			instance = new DataManager(context);
		}
		return instance;
	}
	
	private DataManager(Context context) {
		this.context = context;
		initData();
	}
	
	protected void initData() {
		try {
			data = readDataFile();
		} catch (Exception e) {
			Log.w(getClass().getSimpleName(), "Error reading data file", e);
		}
		if ( data == null ) {
			initDefaultData();
		}
	}

	private void initDefaultData() {
		data = new SoundProfilesData();
		data.addProfile(SoundProfileBuilder.buildSilentProfile(context));
		data.addProfile(SoundProfileBuilder.buildVibrateProfile(context));
		data.addProfile(SoundProfileBuilder.buildNormalProfile(context));
		data.addProfile(SoundProfileBuilder.buildLoudProfile(context));
		saveData();
	}

	public SoundProfilesData loadData() {
		return data;
	}
	
	public SoundProfile loadProfile(int position) {
		return data.getProfile(position);
	}
	
	public void selectProfile(Integer position) {
		data.setSelectedProfileIndex(position);
	}
	
	public int addProfile(SoundProfile profile) {
		int position = data.addProfile(profile);
		return position;
	}

	public void updateProfile(SoundProfile profile, Integer position) {
		data.setProfile(position, profile);
		saveData();
	}

	public void deleteProfile(int position) {
		data.removeProfile(position);
		saveData();
	}

	public boolean saveData() {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			SoundProfileDataSerializer serializer = new SoundProfileDataSerializer();
			serializer.serialize(fos, data);
			fos.flush();
			return true;
		} catch (Exception e) {
			Log.w(getClass().getSimpleName(), "Error writing data file", e);
		} finally {
			if ( fos != null ) {
				try {
					fos.close();
				} catch (IOException e) { }
			}
		}
		return false;
	}
	
	public void resetData() {
		initDefaultData();
	}
	
	private SoundProfilesData readDataFile() throws Exception {
		FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
		InputStreamReader reader = new InputStreamReader(fileInputStream);
		SoundProfilesXmlParser xmlParser = new SoundProfilesXmlParser();
		SoundProfilesData data = xmlParser.parse(reader);
		return data;
	}

}
