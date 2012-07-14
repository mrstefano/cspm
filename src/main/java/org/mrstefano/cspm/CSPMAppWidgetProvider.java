/**
 * 
 */
package org.mrstefano.cspm;

import org.mrstefano.cspm.activity.SelectActivity;
import org.mrstefano.cspm.manager.DataManager;
import org.mrstefano.cspm.model.SoundProfile;
import org.mrstefano.cspm.view.adapter.IconListAdapter;
import org.mrstefano.cspm.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.RemoteViews;

/**
 * @author S. Ricci
 *
 */
public class CSPMAppWidgetProvider extends AppWidgetProvider {

	public static final String WIDGET_IDS_KEY = "spmWidgetIds";
	public static final String WIDGET_DATA_KEY = "spmWidgetData";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	        int[] appWidgetIds) {
	    update(context, appWidgetManager, appWidgetIds, null);
	}

	public void update(Context context, AppWidgetManager appWidgetManager, int[] ids, Object data) {
		final int N = ids.length;

		for (int i=0; i<N; i++) {
			int appWidgetId = ids[i];

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);

			Intent intent = new Intent(context, SelectActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

			views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
			
			DataManager dataManager = DataManager.getInstance(context);
			SoundProfile selectedProfile = dataManager.getSelectedProfile();
			String profileName;
			Integer iconId;
			if ( selectedProfile == null ) {
				profileName = context.getString(R.string.no_profile_selected);
				iconId = null;
			} else {
				profileName = selectedProfile.name;
				iconId = IconListAdapter.getIconId(selectedProfile.icon);
			}
			if ( iconId != null ) {
				views.setImageViewResource(R.id.widget_image, iconId);
			}
			views.setTextViewText(R.id.widget_text, profileName);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (intent.hasExtra(WIDGET_IDS_KEY)) {
	        int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
	        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			if ( intent.hasExtra(WIDGET_DATA_KEY) ) {
	           Object data = intent.getExtras().getParcelable(WIDGET_DATA_KEY);
	           this.update(context, appWidgetManager, ids, data);
	        } else {
	            this.onUpdate(context, appWidgetManager, ids);
	        }
	    } else super.onReceive(context, intent);
	}
	    
	public static void updateAllWidgets(Context context, Parcelable data) {
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	    ComponentName widgetComponentName = new ComponentName(context, CSPMAppWidgetProvider.class);
		int[] ids = appWidgetManager.getAppWidgetIds(widgetComponentName);
		Intent updateIntent = new Intent();
	    updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	    updateIntent.putExtra(CSPMAppWidgetProvider.WIDGET_IDS_KEY, ids);
	    updateIntent.putExtra(CSPMAppWidgetProvider.WIDGET_DATA_KEY, data);
	    context.sendBroadcast(updateIntent);
	}

	public static void updateAllWidgets(Context context) {
		updateAllWidgets(context, null);
	}

}
