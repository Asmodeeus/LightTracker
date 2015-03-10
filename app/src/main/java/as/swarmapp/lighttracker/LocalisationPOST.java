package as.swarmapp.lighttracker;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import as.swarmapp.lighttracker.BaseDeDonnees.DAOPosition;
import as.swarmapp.lighttracker.BaseDeDonnees.Position;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class LocalisationPOST extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_START = "as.swarmapp.lighttracker.action.START";
    public static final String ACTION_STOP = "as.swarmapp.lighttracker.action.STOP";

    // TODO: Rename parameters
    public static final String EXTRA_ADRESSE = "as.swarmapp.lighttracker.extra.ADRESSE";
    public static final String EXTRA_TRACKER = "as.swarmapp.lighttracker.extra.TRACKER";
    public static final String EXTRA_TOKEN = "as.swarmapp.lighttracker.extra.TOKEN";

    private boolean en_cours = true;

    // Pour la localisation
    private GoogleApiClient monClient;
    private LocationRequest monLR = new LocationRequest();

    // Pour le HTTP
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE); // timestamp selon le format de Haggis. Locale.FRANCE sert à écrire les chiffres en ASCII
    public static final String FORMAT_PARAM_ = "%s=%s&";
    public static final String LISTE_ = "liste[%s]";

    // Pour la BDD
    private DAOPosition monDAO = null;

    // Autre
    private String adresse = "";
    private String token = "";
    private long tracker_id = -1;

    public LocalisationPOST() {
        super("LocalisationPOST");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                adresse     = intent.getStringExtra(EXTRA_ADRESSE);
                token       = intent.getStringExtra(EXTRA_TRACKER);
                tracker_id  = intent.getLongExtra(EXTRA_TOKEN, -1);
                handleStart();

            }else if (ACTION_STOP.equals(action)) {
                handleStop();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleStart() {
        en_cours = true;
        buildGoogleApiClient();
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleStop() {
        en_cours = false;

        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /* -------------------- LOCALISATION ---------------*/


    protected synchronized void buildGoogleApiClient() {
        monClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        monClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        monLR.setInterval(5000);
        monLR.setFastestInterval(3000);
        monLR.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                monClient, monLR, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //MAJaffichage(new Position(adresse, tracker_id, token, sdf.format(new Date()), (float) location.getLatitude(), (float) location.getLongitude()));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /* -------------- METHODES HTTP ------------- */
    public String PositionToURL(Position p){
        Map<String, String> paramsKV = new HashMap<>(5);
        paramsKV.put(String.format(LISTE_, DAOPosition.TOKEN), p.getToken());
        paramsKV.put(String.format(LISTE_, DAOPosition.TRACKER_ID), Long.toString(p.getTracker_id()));
        paramsKV.put(String.format(LISTE_, DAOPosition.HORODATE), p.getDatetime());
        paramsKV.put(String.format(LISTE_, DAOPosition.LATITUDE), String.valueOf(p.getLatitude()));
        paramsKV.put(String.format(LISTE_, DAOPosition.LONGITUDE), String.valueOf(p.getLongitude()));

        return mapToParams(paramsKV);
    }

    public static String mapToParams(Map<String, String> laMap){
        String s = "";

        for (String k:laMap.keySet()) {
            s += String.format(FORMAT_PARAM_,k, laMap.get(k));
        }
        if (s.length()-1 >= 0) {
            return s.substring(0, s.length() - 1);
        }
        else
            return "";
    }
}
