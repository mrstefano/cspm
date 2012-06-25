package org.mrstefano.cspm.manager.dataXml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.mrstefano.cspm.model.ApplicationStatics;
import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.model.SoundProfilesData;
import org.mrstefano.cspm.model.StreamSettings;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class SoundProfileDataSerializer {

	public void serialize(FileOutputStream fos, SoundProfilesData data) throws IOException {
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, SoundProfilesDataHandler.DATA_EL);
		serializer.attribute(null, SoundProfilesDataHandler.VERSION_ATTR, ApplicationStatics.VERSION);
		valueToElement(serializer, SoundProfilesDataHandler.SELECTED_PROFILE_EL, data.getSelectedProfileIndex());
		serializer.startTag(null, SoundProfilesDataHandler.PROFILES_EL);
		List<SoundProfile> profiles = data.getProfiles();
		if ( profiles != null ) {
			for (SoundProfile sp : profiles) {
				serialize(serializer, sp);
			}
		}
		serializer.endTag(null, SoundProfilesDataHandler.PROFILES_EL);
		serializer.endTag(null, SoundProfilesDataHandler.DATA_EL);
		serializer.endDocument();
		serializer.flush();
	}

	private void serialize(XmlSerializer serializer, SoundProfile sp)
			throws IOException {
		serializer.startTag(null, SoundProfilesDataHandler.PROFILE_EL);
		valueToElement(serializer, SoundProfilesDataHandler.NAME_EL, sp.name);
		valueToElement(serializer, SoundProfilesDataHandler.ICON_EL, sp.icon);
		Set<Integer> streamTypes = sp.getStreamTypes();
		for (Integer streamType : streamTypes) {
			StreamSettings streamSettings = sp.getStreamSettings(streamType);
			serialize(serializer, streamType, streamSettings);
		}
		serializer.endTag(null, SoundProfilesDataHandler.PROFILE_EL);
	}

	private void serialize(XmlSerializer serializer, Integer streamType,
			StreamSettings streamSettings) throws IOException {
		serializer.startTag(null, SoundProfilesDataHandler.STREAM_SETTINGS_EL);
		valueToElement(serializer, SoundProfilesDataHandler.STREAM_TYPE_EL, streamType);
		valueToElement(serializer, SoundProfilesDataHandler.VOLUME_EL, streamSettings.volume);
		valueToElement(serializer, SoundProfilesDataHandler.VIBRATE_EL, streamSettings.vibrate);
		serializer.endTag(null, SoundProfilesDataHandler.STREAM_SETTINGS_EL);
	}
	
	private void valueToElement(XmlSerializer serializer, String tagName, Object value) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, tagName);
		if ( value != null ) {
			serializer.text(value.toString());
		}
		serializer.endTag(null, tagName);
	}
	
}