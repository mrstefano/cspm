package org.mrstefano.cspm.model;


public class IconListItem {
	
	public final Integer textId;
	public final Integer iconId;
	public final String text;
	
	public IconListItem(Integer iconId) {
		this(iconId, (String) null);
	}
	
	public IconListItem(Integer iconId, Integer textId) {
		this.iconId = iconId;
		this.textId = textId;
		this.text = null;
	}

	public IconListItem(Integer iconId, String text) {
		this.iconId = iconId;
		this.text = text;
		this.textId = null;
	}
	
}
