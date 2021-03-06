package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.helpers.ModelHelper;

/**
 * File created by phil on 8/13/14.
 */
public class Favorite {

    private String userName;
    private String modelKey;
    private int modelEnum;

    public Favorite(){

    }

    public Favorite(String userName, String modelKey, int model_type) {
        this.userName = userName;
        this.modelKey = modelKey;
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

    public int getModelEnum() {
        return modelEnum;
    }

    public ModelHelper.MODELS getModelType(){
        return ModelHelper.MODELS.values()[modelEnum];
    }

    public void setModelEnum(int modelEnum) {
        this.modelEnum = modelEnum;
    }

    public ContentValues getParams(){
        ContentValues cv = new ContentValues();
        cv.put(Database.Favorites.KEY, getKey());
        cv.put(Database.Favorites.USER_NAME, userName);
        cv.put(Database.Favorites.MODEL_KEY, modelKey);
        cv.put(Database.Favorites.MODEL_ENUM, modelEnum);
        return cv;
    }
}
