package as.swarmapp.lighttracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import as.swarmapp.lighttracker.BaseDeDonnees.DAOPosition;
import as.swarmapp.lighttracker.BaseDeDonnees.Position;

public class ServiceLocalisationPOST extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private boolean tracking = true;

    public static ServiceLocalisationPOST leService;

    // Pour le HTTP
    public static final String FORMAT_PARAM_ = "%s=%s&";
    public static final String LISTE_ = "liste[%s]";

    // Pour la localisation
    private GoogleApiClient monClient;
    private LocationRequest monLR = new LocationRequest();

    // Pour la BDD
    private DAOPosition monDAO = DAOPosition.getInstance(this);

    // Autre
    private String adresse = "";
    private String token = "";
    private long tracker_id = -1;

    public ServiceLocalisationPOST() {
    }

    public static void setInstance(ServiceLocalisationPOST slp) throws AssertionError{
        if (leService!=null && leService.tracking){
            throw new AssertionError("Un autre service est déjà en cours de tracking");
        }
        leService = slp;
    }

    public static ServiceLocalisationPOST getInstance() throws NullPointerException{
        if (leService != null) {
            return leService;
        }
        throw new NullPointerException("Aucun service n'existe");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
     public int onStartCommand(Intent intent, int flags, int startId) {
        // Fonction executée lors de l'appel à startService()
        try {
            setInstance(this);
        }catch(AssertionError e){
            return super.onStartCommand(intent, flags, startId);
        }

        if (intent != null) {
            adresse     = intent.getStringExtra(Const.EXTRA_ADRESSE); // adresse à laquelle envoyer les données, directement
            token       = intent.getStringExtra(Const.EXTRA_TOKEN);
            tracker_id  = intent.getLongExtra(Const.EXTRA_TRACKER, -1);

            if (adresse.length()!=0 && token.length()!=0 && tracker_id!=-1) {
                startTracking();

            }else {
                throw new ExceptionInInitializerError("Les paramètres sont incorrects");
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    public void startTracking() {
        if (tracker_id != -1) {
            Log.w("start", "Tracking");
            tracking = true;
            buildGoogleApiClient();
        }else{
            throw new IllegalStateException("Le service n'est pas prêt");
        }

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    public void stopTracking() {
        Log.w("stop", "Tracking");
        tracking = false;
        stopLocationUpdates();
        stopSelf();
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
        parametrerLocationRequest();
        startLocationUpdates();
    }

    protected void parametrerLocationRequest() {
        monLR.setInterval(5000);
        monLR.setFastestInterval(3000);
        monLR.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(monClient, monLR, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Imprevus.rapporterAvertissement(Imprevus.W_API_CONNEXION_SUSPENDUE);
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (location != null) {

            new Thread(){ public void run(){
                Log.w("onLocationChanged", location.getLatitude() + ", "+ location.getLongitude());

                // On crée une nouvelle position à partir de la localisation
                Position laPos = Position.positionFromLocation(adresse, tracker_id, token, location);

                // On ajoute la position à la base de données
                long id_pos = monDAO.ajouter(laPos);

                // On essaie de l'envoyer au serveur
                if (POSTposition(laPos)){

                    // si ça réussit, on la note comme "envoyée"
                    monDAO.setSent(id_pos);

                }
            } }.start();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Imprevus.rapporterErreur(Imprevus.E_API_CONNECTION_FAILED);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(monClient, this);
    }


    /* -------------- METHODES HTTP ------------- */

    public Boolean POSTposition(Position p){

        String params = positionToURL(p);
        Log.w("params", params);
        Boolean ok = false;

        try {
            /*
            // Version POST
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://haggis.ensta-bretagne.fr:3000/listeandroid")).openConnection();
            int longueurParams = params.getBytes().length;
            preparerPourPOST(urlConnection, longueurParams);

            try {
                paramsPOST(urlConnection, params);
                ok = reponseRequetePOST(urlConnection);

            }finally{
                urlConnection.disconnect();
            }
            //*/

            //*
            // Version GET
            Log.w("requête à ", p.getEvent());
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL(p.getEvent()+"?"+params)).openConnection();

            try {
                ok = isRequeteOK(urlConnection);

            }finally{
                urlConnection.disconnect();
            }
            //*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    public static HttpURLConnection preparerPourPOST(HttpURLConnection u, int taille_requete) throws ProtocolException {
        u.setReadTimeout(10000);
        u.setConnectTimeout(15000);
        u.setRequestMethod("POST");
        u.setFixedLengthStreamingMode(taille_requete);
        u.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        u.setRequestProperty("User-Agent", "Mozilla");
        u.setRequestProperty("Accept", "*/*");
        u.setInstanceFollowRedirects(false);
        u.setDoInput(true);
        u.setDoOutput(true);
        return u;
    }

    public static HttpURLConnection paramsPOST(HttpURLConnection u, String params) throws Exception{
        OutputStream osDeURLconn = u.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(osDeURLconn, "UTF-8"));
        writer.write(params);
        writer.flush();
        writer.close();
        osDeURLconn.close();
        return u;
    }

    public static Boolean reponseRequetePOST(HttpURLConnection u) throws Exception{
        InputStream inDeURLconn;
        try {
            inDeURLconn = new BufferedInputStream(u.getInputStream());
            inDeURLconn.close();
            return Boolean.TRUE;

        }catch(Exception e){
            inDeURLconn = new BufferedInputStream(u.getErrorStream());
            inDeURLconn.close();
            return Boolean.FALSE;
        }
        /*
        if (u.getResponseCode() < 400){
            inDeURLconn = new BufferedInputStream(u.getInputStream());
        }else{
            Log.w("code : ", String.valueOf(u.getResponseCode()));
            inDeURLconn = new BufferedInputStream(u.getErrorStream());
        }//*/
    }

    public static Boolean isRequeteOK(HttpURLConnection u) throws Exception{
        switch(u.getResponseCode()){
            case HttpURLConnection.HTTP_OK:
                return true;

            case HttpURLConnection.HTTP_BAD_REQUEST:
                Imprevus.rapporterErreur(Imprevus.E_BAD_REQUEST);

            case HttpURLConnection.HTTP_UNAVAILABLE:
                Imprevus.rapporterErreur(Imprevus.E_SERVICE_UNAVAILABLE);

            default:
                Imprevus.rapporterErreur(Imprevus.INCONNUE);

        }
        return false;
    }

    public String positionToURL(Position p){
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
