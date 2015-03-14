package as.swarmapp.lighttracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import as.swarmapp.lighttracker.BaseDeDonnees.DAOPosition;
import as.swarmapp.lighttracker.BaseDeDonnees.Position;

public class ServiceLocalisationPOST extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private boolean tracking = true;

    public static ServiceLocalisationPOST leService;

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
            Log.i("ServiceLocalisationPOST", "Start tracking");
            tracking = true;
            buildGoogleApiClient();

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Track.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("LightTracker")
                    .setContentText("Background tracking enabled")
                    .setSmallIcon(R.drawable.ic_service_track)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(contentIntent)
                    .build();
            startForeground(1, notification);
            /*
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, class), 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setWhen(System.currentTimeMillis())
                    .addAction(R.drawable.ic_action_search, "title", contentIntent)
                    .build();
            Log.w("onHandleIntent", "startForeground");
            startForeground(1, notification);
            //*/

        }else{
            throw new IllegalStateException("Le service n'est pas prêt");
        }

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    public void stopTracking() {
        Log.i("ServiceLocalisationPOST", "Stop tracking");
        tracking = false;
        stopLocationUpdates();
        stopForeground(true);
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
                Log.i("onLocationChanged", location.getLatitude() + ", "+ location.getLongitude());

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

        String url = p.getEvent();
        String params = p.toParams();
        Boolean ok = false;
        try {
            //*
            // Version POST
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL(url)).openConnection();
            int longueurParams = params.getBytes().length;
            Utiles.preparerPourPOST(urlConnection, longueurParams);

            try {
                Utiles.paramsPOST(urlConnection, params);
                ok = Utiles.isRequeteOK(urlConnection);
                //ok = reponseRequetePOST(urlConnection);

            }catch (UnknownHostException e){
                Imprevus.rapporterAvertissement(Imprevus.W_CONNEXION_PERDUE);

            }finally{
                urlConnection.disconnect();
            }
            //*/

            /*
            // Version GET
            url = url + "?" + params;
            Log.w("GET", "["+url+"]");
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL(url)).openConnection();

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
}
