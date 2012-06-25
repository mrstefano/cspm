package org.mrstefano.cspm.model;

import org.mrstefano.cspm.R;

import android.content.Context;
import android.widget.Toast;

public class ProfileValidator {

	private Context context;

	public ProfileValidator(Context context) {
		super();
		this.context = context;
	}
	
	public boolean validate(SoundProfile profile) {
		int errorMessageRid = -1;
		if ( profile.name == null || profile.name.trim().length() == 0 ) {
			errorMessageRid = R.string.edit_validation_name_not_specified;
		}
		if ( errorMessageRid > 0 ) {
			showErrorMessage(errorMessageRid);
			return false;
		} else {
			return true;
		}
	}

	private void showErrorMessage(int messageRid) {
		Toast.makeText(context, messageRid, Toast.LENGTH_SHORT);
	}

}
