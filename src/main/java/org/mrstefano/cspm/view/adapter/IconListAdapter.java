package org.mrstefano.cspm.view.adapter;

import java.util.List;

import org.mrstefano.cspm.model.IconListItem;
import org.mrstefano.cspm.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

public class IconListAdapter extends ArrayAdapter<IconListItem> {

	public static final String ICON_DEFAULT = "default";
	public static final String ICON_LOUD = "loud";
	public static final String ICON_NORMAL = "normal";
	public static final String ICON_LOW = "low";
	public static final String ICON_VIBRATE = "vibrate";
	public static final String ICON_SILENT = "silent";
	
	private static final int ICON_PADDING = 5;

	public IconListAdapter(Context context, int textViewResourceId,
			IconListItem[] objects) {
		super(context, textViewResourceId, objects);
	}

	public IconListAdapter(Context context, int resource,
			int textViewResourceId, IconListItem[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public IconListAdapter(Context context, int resource,
			int textViewResourceId, List<IconListItem> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public IconListAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public IconListAdapter(Context context, int textViewResourceId,
			List<IconListItem> objects) {
		super(context, textViewResourceId, objects);
	}

	public IconListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		//User super class to create the View
		CheckedTextView view = (CheckedTextView) super.getView(position, convertView, parent);
            
		//Put the image on the TextView
		IconListItem item = getItem(position);
		if ( item.iconId != null ) {
			view.setCompoundDrawablesWithIntrinsicBounds(item.iconId, 0, 0, 0);
		}
		if ( item.textId != null ) {
			view.setText(item.textId);
		} else {
			view.setText(item.text);
		}
		
		//Add margin between image and text (support various screen densities)
		int iconPadding = (int) (ICON_PADDING * getContext().getResources().getDisplayMetrics().density + 0.5f);
		view.setCompoundDrawablePadding(iconPadding);

		return view;
	}
	
	public static Integer getIconId(String iconName) {
    	//iconId = getResources().getIdentifier(profile.icon, "drawable", getPackageName());
		if ( iconName != null ) {
			if ( ICON_SILENT.equals(iconName) ) {
				return R.drawable.silent;
			} else if ( ICON_VIBRATE.equals(iconName) ) {
				return R.drawable.vibrate;
			} else if ( ICON_LOW.equals(iconName) ) {
				return R.drawable.low;
			} else if ( ICON_NORMAL.equals(iconName) ) {
				return R.drawable.normal;
			} else if ( ICON_LOUD.equals(iconName) ) {
				return R.drawable.loud;
			}
		}
		return R.drawable.profile_icon_default;
	}
	
	public static String getIconName(int iconId) {
		switch ( iconId ) {
		case R.drawable.low:
			return ICON_LOW;
		case R.drawable.normal:
			return ICON_NORMAL;
		case R.drawable.loud:
			return ICON_LOUD;
		case R.drawable.vibrate:
			return ICON_VIBRATE;
		case R.drawable.silent:
			return ICON_SILENT;
		default:
			return null;
		}
	}


}
