package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;

/**
 * Created by Nathan on 4/30/2014.
 */
public class DataManager {

    public static Team getTeam(Context c, String teamKey) throws NoDataException {
        Database db = Database.getInstance(c);
        boolean existsInDb = db.getTeamsTable().exists(teamKey);
        boolean connectedToInternet = ConnectionDetector.isConnectedToInternet(c);
        if (existsInDb) {
            if (connectedToInternet) {
                // We are connected to the internet and have a record in the database.
                // Check if the local copy is up-to-date; if it is, return it.
                // Otherwise, requery the API, cache the new data, and return the data.
                // TODO: once we support the If-Modified-Since header, use that to check if our local copy is up-to-date.
                // For now, we just load the new data every time.

                // Team team = TBAv2.getTeam(teamKey);
                //db.getTeamsTable().update(team);
                Log.d("datamanager", "Online; loaded from database");
                Team team = db.getTeamsTable().get(teamKey);
                System.out.println("events: " + team.getEvents().toString());
                return team;
            } else {
                Log.d("datamanager", "Offline; loaded from database");
                return db.getTeamsTable().get(teamKey);
            }
        } else {
            if (connectedToInternet) {
                // Load team data, cache it in the database, return it to caller
                Team team = TBAv2.getTeam(teamKey);
                db.getTeamsTable().add(team);
                Log.d("datamanager", "Online; loaded from internet");
                return team;
            } else {
                // There is no locally stored data and we are not connected to the internet.
                Log.d("datamanager", "Offline; no data!");
                throw new NoDataException("There is no internet connection and no local cache for this team!");
            }
        }
    }

    public static ArrayList<SimpleEvent> getEventsForTeamInYear(Context c, String teamKey, int year) throws NoDataException {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        // This will throw an exception if there is no local data and no internet connection
        // We want this to propagate up the stack
        Team team = getTeam(c, teamKey);
        JsonArray jsonEvents = team.getEvents();
        for (int i = 0; i < jsonEvents.size(); i++) {
            JsonObject currentEvent = jsonEvents.get(i).getAsJsonObject();
            SimpleEvent event = JSONManager.getGson().fromJson(currentEvent,SimpleEvent.class);
            events.add(event);
        }
        return events;
    }

    public static ArrayList<SimpleEvent> getEventsForCompetitionWeek(Context c, int year, int week) throws NoDataException{
        Database db = Database.getInstance(c);
        ArrayList<SimpleEvent> databaseEvents = db.getEventsTable().getAll(year,week);
        boolean existsInDb = databaseEvents.size()>0;
        boolean connectedToInternet = ConnectionDetector.isConnectedToInternet(c);
        if (existsInDb) {
            if (connectedToInternet) {
                // We are connected to the internet and have a record in the database.
                // Check if the local copy is up-to-date; if it is, return it.
                // Otherwise, requery the API, cache the new data, and return the data.
                // TODO: once we support the If-Modified-Since header, use that to check if our local copy is up-to-date.
                // For now, we just load from the database every time

                Log.d("datamanager", "Online; loaded from database");
                return databaseEvents;
            } else {
                Log.d("datamanager", "Offline; loaded from database");
                return databaseEvents;
            }
        } else {
            if (connectedToInternet) {
                // Load team data, cache it in the database, return it to caller
                ArrayList<SimpleEvent> loadedEvents = TBAv2.getEventList(year);
                db.getEventsTable().add(loadedEvents);
                Log.d("datamanager", "Online; loaded from internet");
                return db.getEventsTable().getAll(year,week);
            } else {
                // There is no locally stored data and we are not connected to the internet.
                Log.d("datamanager", "Offline; no data!");
                throw new NoDataException("There is no internet connection and no local cache for this team!");
            }
        }
    }

    public static class NoDataException extends Exception {
        public NoDataException(String message) {
            super(message);
        }

        public NoDataException(String message, Throwable t) {
            super(message, t);
        }
    }
}
