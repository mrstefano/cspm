package org.mrstefano.cspm.manager.dataXml;

import java.util.ArrayList;
import java.util.List;

import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.model.SoundProfilesData;
import org.mrstefano.cspm.model.StreamSettings;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SoundProfilesDataHandler extends DefaultHandler {

	public static final String VERSION_ATTR = "version";
	public static final String DATA_EL = "data";
	public static final String SELECTED_PROFILE_EL = "selected_profile";
	public static final String PROFILES_EL = "profiles";
	public static final String PROFILE_EL = "profile";
	public static final String NAME_EL = "name";
	public static final String STREAM_SETTINGS_EL = "stream_settings";
	public static final String VOLUME_EL = "volume";
	public static final String VIBRATE_EL = "vibrate";
	public static final String STREAM_TYPE_EL = "stream_type";
	public static final String ICON_EL = "icon";
	public static final String HAPTIC_FEEDBACK_ENABLED_EL = "haptick_feedback_enabled";

	private StringBuffer buffer = new StringBuffer();
	private SoundProfilesData data;
	private List<SoundProfile> profiles;
	private SoundProfile profile;
	private StreamSettings streamSettings;
	private Integer streamType;

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		buffer.setLength(0);
		if (localName.equals(DATA_EL)) {
			data = new SoundProfilesData();
			//String version = atts.getValue(VERSION_ATTR);
			//todo version manage
		} else if (localName.equals(PROFILES_EL)) {
			profiles = new ArrayList<SoundProfile>();
		} else if (localName.equals(PROFILE_EL)) {
			profile = new SoundProfile();
		} else if (localName.equals(STREAM_SETTINGS_EL)) {
			streamSettings = new StreamSettings();
		} else if (localName.equals(STREAM_TYPE_EL)) {
			streamType = extractIntegerValue();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException {
		if (localName.equals(PROFILES_EL)) {
			data.setProfiles(profiles);
		} else if (localName.equals(SELECTED_PROFILE_EL)) {
			data.setSelectedProfileIndex(extractIntegerValue());
		} else if (localName.equals(PROFILE_EL)) {
			profiles.add(profile);
		} else if (localName.equals(NAME_EL)) {
			profile.name = buffer.toString();
		} else if (localName.equals(ICON_EL)) {
			profile.icon = buffer.toString();
		} else if (localName.equals(HAPTIC_FEEDBACK_ENABLED_EL)) {
			profile.haptickFeedbackEnabled = Boolean.parseBoolean(buffer.toString());
		} else if (localName.equals(STREAM_SETTINGS_EL)) {
			profile.putStreamSetting(streamType, streamSettings);
		} else if (localName.equals(STREAM_TYPE_EL)) {
			streamType = extractIntegerValue();
		} else if (localName.equals(VOLUME_EL)) {
			streamSettings.volume = extractIntegerValue();
		} else if (localName.equals(VIBRATE_EL)) {
			streamSettings.vibrate = Boolean.parseBoolean(buffer.toString());
		}
	}

	private Integer extractIntegerValue() {
		String string = buffer.toString();
		if ( string != null && ! string.equals("")) {
			return Integer.parseInt(string);
		} else {
			return null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public SoundProfilesData retrieveData() {
		return data;
	}

}
