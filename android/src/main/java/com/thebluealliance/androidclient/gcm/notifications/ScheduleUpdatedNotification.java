package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;

import java.util.Date;

/**
 * Created by phil on 11/21/14.
 */
public class ScheduleUpdatedNotification extends BaseNotification {

    private String eventName, eventKey;
    private JsonElement matchTime;

    public ScheduleUpdatedNotification(String messageData) {
        super(NotificationTypes.SCHEDULE_UPDATED, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONManager.getasJsonObject(messageData);
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name");
        }
        eventName = jsonData.get("event_name").getAsString();
        if (!jsonData.has("event_key")) {
            throw new JsonParseException("Notification data does not contain 'event_key");
        }
        eventKey = jsonData.get("event_key").getAsString();

        if (!jsonData.has("first_match_time")) {
            throw new JsonParseException("Notification data does not contain 'first_match_time");
        }
        matchTime = jsonData.get("first_match_time");
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

        String firstMatchTime = null;
        if (!matchTime.isJsonNull()) {
            Date date = new Date(matchTime.getAsLong() * 1000L);
            java.text.DateFormat format = DateFormat.getTimeFormat(context);
            firstMatchTime = format.format(date);
        }

        String contentText;
        if (firstMatchTime == null) {
            contentText = String.format(r.getString(R.string.notification_schedule_updated_without_time), eventName);
        } else {
            contentText = String.format(r.getString(R.string.notification_schedule_updated_with_time), eventKey, firstMatchTime);
        }

        PendingIntent intent = PendingIntent.getActivity(context, 0, ViewEventActivity.newInstance(context, eventKey), 0);

        NotificationCompat.Builder builder = getBaseBuilder(context)
                .setContentTitle(r.getString(R.string.notification_schedule_updated_title))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_access_time_white_24dp))
                .setContentIntent(intent)
                .setAutoCancel(true);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally(Context c) {
        /* This notification has no data that we can store locally */
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + eventKey).hashCode();
    }
}
