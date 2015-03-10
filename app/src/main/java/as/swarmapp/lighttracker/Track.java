package as.swarmapp.lighttracker;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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


public class Track extends ActionBarActivity implements GestionHorsUI,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // TODO FIXME A transformer en service.

    // Pour le HTTP
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE); // timestamp selon le format de Haggis. Locale.FRANCE sert à écrire les chiffres en ASCII
    public static final String FORMAT_PARAM_ = "%s=%s&";
    public static final String LISTE_ = "liste[%s]";

    // Pour la BDD
    private DAOPosition monDAO = null;

    // Pour la localisation
    private GoogleApiClient monClient;
    private LocationRequest monLR = new LocationRequest();

    // Autre
    private String adresse = "";
    private String token = "";
    private long tracker_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        String donnees[] = getIntent().getStringArrayExtra(Const.DONNEES);

        if (donnees != null) {
            adresse = donnees[0];
            token = donnees[1];
            //TODO tracker_id = Long.valueOf(donnees[2]);
            monDAO = new DAOPosition(this);
            buildGoogleApiClient();

        }else{
            new Exception("Cette configuration n'est pas censée arriver").printStackTrace();
            finish();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        monClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        monClient.connect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            MAJaffichage(new Position(adresse, tracker_id, token, sdf.format(new Date()), (float) location.getLatitude(), (float) location.getLongitude()));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void MAJaffichage(final Object laPosition) {
        final String paramPOST = PositionToURL((Position) laPosition);
        new Thread(new Runnable() { public void run() {
            Object reponsePOST = aFaireHorsUI(laPosition);
            aFaireEnUI(reponsePOST);

        } }).start();
    }

    @Override
    public Object aFaireHorsUI(Object o) {
        return null;
    }

    @Override
    public void aFaireEnUI(Object o) {

    }


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
