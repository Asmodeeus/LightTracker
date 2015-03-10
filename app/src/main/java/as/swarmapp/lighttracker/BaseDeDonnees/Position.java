package as.swarmapp.lighttracker.BaseDeDonnees;

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
}
