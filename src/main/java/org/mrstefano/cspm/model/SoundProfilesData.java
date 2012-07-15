package org.mrstefano.cspm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoundProfilesData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<SoundProfile> profiles;

	private Integer selectedProfileIndex;

	public SoundProfilesData() {
		profiles = new ArrayList<SoundProfile>();
	}
	
	public int addProfile(SoundProfile profile) {
		int position = profiles.size();
		profiles.add(profile);
		return position;
	}
	
	public String[] getProfileNames() {
		int size = profiles.size();
		String[] profileNames = new String[size];
		for ( int i = 0; i < size; i ++ ) {
			SoundProfile profile = profiles.get(i);
			profileNames[i] = profile.name;
		}
		return profileNames;
	}

	public SoundProfile getSelectedProfile() {
		if (selectedProfileIndex != null ) {
			return profiles.get(selectedProfileIndex);
		} else {
			return null;
		}
	}

	public SoundProfile getProfile(int index) {
		return profiles.get(index);
	}

	public Integer getSelectedProfileIndex() {
		return selectedProfileIndex;
	}

	public void setSelectedProfileIndex(Integer selectedProfileIndex) {
		this.selectedProfileIndex = selectedProfileIndex;
	}

	public List<SoundProfile> getProfiles() {
		return Collections.unmodifiableList(profiles);
	}

	public void setProfiles(List<SoundProfile> profiles) {
		this.profiles = profiles;
	}

	public void setProfile(Integer index, SoundProfile profile) {
		this.profiles.set(index, profile);
	}

	public void removeProfile(int index) {
		this.profiles.remove(index);
	}

}
