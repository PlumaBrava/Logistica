package com.nextnut.logistica.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.nextnut.logistica.R;

/**
 * Implementation of App Widget functionality.
 */
public class LogisticaWidget extends AppWidgetProvider {
    public static final String LOG_TAG = LogisticaWidget.class.getSimpleName();
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetIds) {
        Log.i(LOG_TAG, "updateAppWidget");

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_logistica);



        //*****


//        for (int appWidgetId=0; appWidgetId< appWidgetIds;appWidgetId++) {

//            // Set up the intent that starts the StackViewService, which will
//            // provide the views for this collection.
//            Intent intent = new Intent(context, LogisticaWidgetService.class);
//            // Add the app widget ID to the intent extras.
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//            // Instantiate the RemoteViews object for the app widget layout.
//            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//            // Set up the RemoteViews object to use a RemoteViews adapter.
//            // This adapter connects
//            // to a RemoteViewsService  through the specified intent.
//            // This is how you populate the data.
//            rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);




            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_logistica);
//            views.setRemoteAdapter(appWidgetId, R.id.widget_list, intent);
            views.setTextViewText(R.id.appwidget_text, widgetText);


            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
                Log.i(LOG_TAG, "Set the remote Id Adapter");
            } else {
                setRemoteAdapterV11(context, views);
            }
//            boolean useDetailActivity = context.getResources()
//                    .getBoolean(R.bool.use_detail_activity);
//            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
//                    ? new Intent(context, DetailActivity.class)
////                    : new Intent(context, MainActivity.class);
//            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
//                    .addNextIntentWithParentStack(clickIntentTemplate)
//                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);

            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }



        //*****



        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, LogisticaWidgetService.class));
        Log.i(LOG_TAG, "Set remoteAdapte ICE cream");
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, LogisticaWidgetService.class));
        Log.i(LOG_TAG, "Set remoteAdapterV11");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(LOG_TAG, "onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.i(LOG_TAG, "onUpdate-appWidgetId"+appWidgetId);


            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        Log.i(LOG_TAG, "LLego Mensaje: " + intent.getAction());

        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            Log.i(LOG_TAG, "LLego Mensaje: entro if " );
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }


        @Override
    public void onEnabled(Context context) {
        Log.i(LOG_TAG, "onEnabled");

        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(LOG_TAG, "onDisabled");

        // Enter relevant functionality for when the last widget is disabled
    }

    public static void upDateWitget (Context context){
        Intent dataUpdateIntent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        context.sendBroadcast(dataUpdateIntent);
    }
}

