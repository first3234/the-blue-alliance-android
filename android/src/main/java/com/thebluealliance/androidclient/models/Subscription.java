package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.ModelHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 8/13/14.
 */
public class Subscription {
    private String userName;
    private String modelKey;
    private String notificationSettings;
    private List<String> notificationList;
    private int modelEnum;

    public Subscription(){
        notificationList = new ArrayList<>();
    }

    public Subscription(String userName, String modelKey, List<String> notificationSettings, int model_type) {
        this.userName = userName;
        this.modelKey = modelKey;
        this.notificationList = notificationSettings;
        this.notificationSettings = makeNotificationJSON(notificationSettings);
        setModelEnum(model_type);
    }

    public String getKey(){
        return userName+":"+modelKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getModelKey() {
        return modelKey;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    public String getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(String notificationSettings) {
        this.notificationSettings = notificationSettings;
        // Update the ArrayList
        notificationList.clear();
        for (JsonElement element : JSONManager.getasJsonArray(notificationSettings)) {
            notificationList.add(element.getAsString());
        }
    }

    public List<String> getNotificationList() {
        if(notificationList == null){
            notificationList = new ArrayList<>();
            for (JsonElement element : JSONManager.getasJsonArray(notificationSettings)) {
                notificationList.add(element.getAsString());
            }
        }
        return notificationList;
    }

    public int getModelEnum() {
        return modelEnum;
    }

    public ModelHelper.MODELS getModelType(){
        return ModelHelper.MODELS.values()[modelEnum];
    }

    public void setModelEnum(int modelEnum) {
        this.modelEnum = modelEnum;
    }

    private static String makeNotificationJSON(List<String> input){
        JsonArray out = new JsonArray();
        for(String s: input){
            out.add(new JsonPrimitive(s));
        }
        return out.toString();
    }

    public ContentValues getParams(){
        ContentValues cv = new ContentValues();
        cv.put(Database.Subscriptions.KEY, getKey());
        cv.put(Database.Subscriptions.USER_NAME, userName);
        cv.put(Database.Subscriptions.MODEL_KEY, modelKey);
        cv.put(Database.Subscriptions.NOTIFICATION_SETTINGS, notificationSettings);
        cv.put(Database.Subscriptions.MODEL_ENUM, modelEnum);
        return cv;
    }
}
