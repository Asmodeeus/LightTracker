package as.swarmapp.lighttracker;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import as.swarmapp.lighttracker.BaseDeDonnees.DAOPosition;
import as.swarmapp.lighttracker.BaseDeDonnees.Position;

/**
 * Created by asmodeeus on 09/03/15.
 */
public final class Utiles {


    public static String streamToString(InputStream in){
        int n;
        StringBuilder sb = new StringBuilder();
        try{
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            while ((n = isr.read()) != -1) {
                sb.append((char)n);
            }
            in.close();

        }catch (IOException e){
            e.printStackTrace();
            return "";

        }
        return sb.toString();
    }

    // ---------- Affichage
    public static List<String> listePositionToListeString(List<Position> lp){
        List<String> toR = new ArrayList<>();
        for (Position p:lp){
            toR.add(p.toString());
        }
        return toR;
    }

    public static String listePositionToString(List<Position> lp, String separateur){
        List<String> lS = listePositionToListeString(lp);
        String toR = Position.colonnesFichierDump();
        for (String s:lS){
            toR = toR + separateur + s;
        }
        return toR;
    }

    // ---------- Dump
    public static String listePositionToStringForDump(List<Position> lp){
        String toR = Position.colonnesFichierDump();
        for (Position p:lp){
            toR = toR + "\n" + p.toStringForDump();
        }
        return toR;
    }
    // ----------

    public static String mapToParams(Map<String, String> laMap){
        String s = "";

        for (String k:laMap.keySet()) {
            s += String.format(Const.FORMAT_PARAM_,k, laMap.get(k));
        }
        if (s.length()-1 >= 0) {
            return s.substring(0, s.length() - 1);
        }
        else
            return "";
    }

    /** Méthode permettant de transformer une liste de positions (par exemple obtenue par DAOPosition.listePosition(évènement X, onlyNotSent) ) en le corps des requêtes POST. </br>
     * A priori le tableau résultant est de taille 1, mais cette méthode permet le cas où les positions correspondent à plusieurs tracker_id. </br>
     * (Typiquement, si le terminal a été utilisé pour tracker différents compétiteurs)
     *
     * @param lp : liste des positions à dumper d'un évènement
     * @return : un tableau de String dont chaque ligne est le corps d'une requête POST
     */
    public static String[] listePositionToPOSTparams(List<Position> lp){

        // On crée une table : < tracker_id, table des paramètres >
        HashMap<String, HashMap<String, String>> mapTrackersParams = new HashMap<>();
        String tracker;

        for(Position p : lp){
            tracker = p.getToken() + Const.STR_SPLIT + Long.toString(p.getTracker_id());

            if (mapTrackersParams.containsKey(tracker)){
                // C'est déjà un tracker connu : on ajoute cette position aux paramètres de sa requête POST
                HashMap<String, String> mapParams = mapTrackersParams.get(tracker);
                mapParams.put(String.format(Const.LISTE_, DAOPosition.HORODATE), mapParams.get(String.format(Const.LISTE_, DAOPosition.HORODATE)) + Const.SPLIT_LISTE + p.getDatetime());
                mapParams.put(String.format(Const.LISTE_, DAOPosition.LATITUDE), mapParams.get(String.format(Const.LISTE_, DAOPosition.LATITUDE)) + Const.SPLIT_LISTE + Float.toString(p.getLatitude()));
                mapParams.put(String.format(Const.LISTE_, DAOPosition.LONGITUDE), mapParams.get(String.format(Const.LISTE_, DAOPosition.LONGITUDE)) + Const.SPLIT_LISTE + Float.toString(p.getLongitude()));

            }else{
                // On doit créer une nouvelle entrée dans la mapTrackersParams
                    // On crée la map des paramètres et on y met la première position pour ce tracker
                HashMap<String, String> mapParams = new HashMap<>(5);
                mapParams.put(String.format(Const.LISTE_, DAOPosition.TOKEN), p.getToken());
                mapParams.put(String.format(Const.LISTE_, DAOPosition.TRACKER_ID), Long.toString(p.getTracker_id()));
                mapParams.put(String.format(Const.LISTE_, DAOPosition.HORODATE), p.getDatetime());
                mapParams.put(String.format(Const.LISTE_, DAOPosition.LATITUDE), Float.toString(p.getLatitude()));
                mapParams.put(String.format(Const.LISTE_, DAOPosition.LONGITUDE), Float.toString(p.getLongitude()));

                    // On l'ajoute à la table des requêtes associées à chaque token
                mapTrackersParams.put(p.getToken() + Const.STR_SPLIT + Long.toString(p.getTracker_id()), mapParams);

            }
        }

        // Résultat : une liste de String à injecter dans autant de requêtes POST
        int ii = 0;
        String toR[] = new String[mapTrackersParams.size()];

        for (String clé : mapTrackersParams.keySet()){
            // Le corps de la ii ème requête POST sera mapToParams( map(ii ème tracker) )
            toR[ii] = Utiles.mapToParams( mapTrackersParams.get(clé) );
            ii++;
        }


        return toR;
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    public static File getUnFichierDeDump() throws FileNotFoundException   {
        if (isExternalStorageWritable()) {
            //  Récupère le dossier à l'emplacement par défaut du dossier "documents"
            File dossierDumps = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Const.DOSSIER_DUMPS);

            // Créer le dossier s'il n'existe pas
            if (!dossierDumps.exists()) {
                if (!dossierDumps.mkdirs()) {
                    throw new FileNotFoundException(Const.ERR_DOSSIER_DUMP);
                }
            }
            return new File(dossierDumps.getPath() + File.separator + "dump" + Const.SDFrequetes.format(new Date()) + ".txt");

        }else{
            throw new FileNotFoundException(Const.ECHEC_ACCES_SD);
        }
    }

    private static void toast(final Activity c, final String message, final int longueur){
        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(c, message, longueur).show();
            }
        });
    }

    /** Affiche un toast de longueur LENGTH_SHORT sur le Thread UI : peut être utilisé dans un autre thread
     *
     * @param c         : l'activité invoquante
     * @param message   : le message du Toast
     */
    public static void toastCourt(Activity c, String message){
        toast(c, message, Toast.LENGTH_SHORT);
    }

    /** Affiche un toast de longueur LENGTH_LONG sur le Thread UI : peut être utilisé dans un autre thread
     *
     * @param c         : l'activité invoquante
     * @param message   : le message du Toast
     */
    public static void toastLong(Activity c, String message){
        toast(c, message, Toast.LENGTH_LONG);
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

    public static Boolean isRequeteOK(HttpURLConnection u) throws Exception{
        int c = u.getResponseCode();
        switch(c){
            case HttpURLConnection.HTTP_OK:
                return true;

            case HttpURLConnection.HTTP_BAD_REQUEST:
                Imprevus.rapporterErreur(Imprevus.E_BAD_REQUEST);
                break;

            case HttpURLConnection.HTTP_UNAVAILABLE:
                Imprevus.rapporterErreur(Imprevus.E_SERVICE_UNAVAILABLE);
                break;

            default:
                Imprevus.rapporterErreur(Imprevus.INCONNUE);
                break;

        }
        return false;
    }
}
