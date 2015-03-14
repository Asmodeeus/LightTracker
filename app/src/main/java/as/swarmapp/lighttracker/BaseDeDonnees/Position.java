package as.swarmapp.lighttracker.BaseDeDonnees;

import android.location.Location;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import as.swarmapp.lighttracker.Const;
import as.swarmapp.lighttracker.Utiles;

/**
 * Created by asmodeeus on 10/03/15.
 */
public class Position {
    // Noms des champs de la table
    private long    id;
    private String  event;
    private long    tracker_id;
    private String  token;
    private String  datetime;
    private float   lati;
    private float   longi;
    private boolean toSend;


    public Position(long id, String event, long tracker_id, String token, String datetime, float lati, float longi, boolean toSend) {
        this.id         = id;
        this.event      = event;
        this.tracker_id = tracker_id;
        this.token      = token;
        this.datetime   = datetime;
        this.lati       = lati;
        this.longi      = longi;
        this.toSend     = toSend;
    }

    public Position(String event, long tracker_id, String token, String datetime, float lati, float longi) {
        this(-1, event, tracker_id, token, datetime, lati, longi, true);
    }

    public static Position positionFromLocation(String event, long tracker_id, String token, Location l){
        return new Position(event, tracker_id, token, Const.SDFrequetes.format(new Date()),(float) l.getLatitude(),(float) l.getLongitude());
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getTracker_id() {
        return tracker_id;
    }

    public float getLatitude() {
        return lati;
    }

    public float getLongitude() {
        return longi;
    }

    public String getEvent() {
        return event;
    }

    public String getToken() {
        return token;
    }

    public String getDatetime() {
        return datetime;
    }

    public boolean isToSend() {
        return toSend;
    }

    /*
    @Override
    public String toString() {
        String horodate = datetime;
        try{
            horodate = Const.SDFbdd.format(Const.SDFrequetes.parse(datetime));
        }catch(ParseException e){
            e.printStackTrace();
        }
        return "[ latitude=" + lati +
                ", longitude=" + longi +
                " (" + horodate + ")]";
    }
    //*/

    public String toParams(){
        Map<String, String> paramsKV = new HashMap<>(5);
        paramsKV.put(String.format(Const.LISTE_, DAOPosition.TOKEN), token);
        paramsKV.put(String.format(Const.LISTE_, DAOPosition.TRACKER_ID), Long.toString(tracker_id));
        paramsKV.put(String.format(Const.LISTE_, DAOPosition.HORODATE), datetime);
        paramsKV.put(String.format(Const.LISTE_, DAOPosition.LATITUDE), String.valueOf(lati));
        paramsKV.put(String.format(Const.LISTE_, DAOPosition.LONGITUDE), String.valueOf(longi));

        return Utiles.mapToParams(paramsKV);
    }

    public static String colonnesFichierDump() {
        return String.format("%s\t%s\t%16s\t%9s\t%s\n", Const.TRACKER_ID, Const.DATETIME, Const.LATITUDE, Const.LONGITUDE, Const.TOKEN);
    }

    public String toStringForDump() {
        String horodate = datetime;
        try{
            horodate = Const.SDFbdd.format(Const.SDFrequetes.parse(datetime));
        }catch(ParseException e){
            e.printStackTrace();
        }
        return String.format("%d\t%s\t% f\t% 10f\t%10s\n", tracker_id, horodate, lati, longi, token);
    }

    //*
    @Override
    public String toString() {
        String horodate = datetime;
        try{
            horodate = Const.SDFbdd.format(Const.SDFrequetes.parse(datetime));
        }catch(ParseException e){
            e.printStackTrace();
        }
        return MessageFormat.format("id : {0} ({1}), coord : {2}, {3}", tracker_id, horodate, lati, longi);
    }
    //*/
}
