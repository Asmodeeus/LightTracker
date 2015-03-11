package as.swarmapp.lighttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;


public class Principale extends ActionBarActivity implements GestionHorsUI {
    public static String   CHECK_PHRASE    = "Hello ! Everything seems fine around here ;)";
    public static String   CHECK_PAGE      = "/what";
    public static String   PARAMS_TRACK_TOKEN = "?" + Const.TRACKER_ID + "=%s&" + Const.TOKEN + "=%s";
    public static int      SITE            = 0;
    public static int      TOKEN           = 1;
    public static int      TRACKER_ID      = 2;
    private static String   site_debug      = "http://haggis.ensta-bretagne.fr:3000"; //FIXME : à retirer
    private static String   token_debug     = "705907f6964d8565573dd3ee73775831"; //FIXME : à retirer
    private static String   tracker_debug   = "12"; //FIXME : à retirer

    // Sémaphore interdisant d'executer plusieurs requêtes en même temps
    private boolean         requeteEnCours  = false;
    private SharedPreferences 		sharedPref;
    private EditText eSite;
    private EditText eToken;
    private EditText eTracker;

    private View.OnClickListener OCLBgo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!requeteEnCours) {
                MAJaffichage(null);

            }else{
                Toast.makeText(Principale.this, Const.REQUETE_EN_COURS, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principale);

        // Récupération des SharedPreferences (qui donnent les données pré-remplies)
        sharedPref 	= getSharedPreferences(Const.PREFERENCES, Context.MODE_PRIVATE);
        // Initialisation du sémaphore de requêtes
        requeteEnCours = false;


        eSite       = (EditText) findViewById(R.id.Esite);
        eToken      = (EditText) findViewById(R.id.Etoken);
        eTracker    = (EditText) findViewById(R.id.Etracker);

        eSite       .setText(sharedPref.getString(Const.PREF_SITE, "" + site_debug));
        eToken      .setText(sharedPref.getString(Const.PREF_TOKEN, "" + token_debug));
        eTracker    .setText(sharedPref.getString(Const.PREF_TRACKER, "" + tracker_debug));

        (findViewById(R.id.Bgo)).setOnClickListener(OCLBgo);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.parametres:
                return true;
            //break;

            case R.id.recuperation_donnees:
                startActivity(new Intent(Principale.this, Recuperation.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String[] donneesAreOk(){
        String site = eSite.getText().toString();
        String token = eToken.getText().toString().toLowerCase();
        String tracker_id = eTracker.getText().toString();
        String prefixe = "http://";

        if (site.startsWith("http://")){
            site = site.substring(7);

        }else if (site.startsWith("https://")){
            site = site.substring(8);
            prefixe = "https://";
        }

        if (site.matches(Const.REGEX_SITE)){
            if (token.matches(Const.REGEX_TOKEN)){
                if (tracker_id.matches(Const.REGEX_TRACKER_ID)){
                    return new String[]{prefixe + site, token, tracker_id};

                }else{
                    Toast.makeText(this, String.format(Const.INVALID_,getString(R.string.Ttracker)), Toast.LENGTH_SHORT).show();

                }
            }else{
                Toast.makeText(this, String.format(Const.INVALID_,getString(R.string.Ttoken)), Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(this, String.format(Const.INVALID_,getString(R.string.Tsite)), Toast.LENGTH_SHORT).show();

        }
        return null;
    }

    @Override
    public void MAJaffichage(Object o) {
        final String donnees[] = donneesAreOk();
        if (donnees != null) {
            requeteEnCours = true;
            Toast.makeText(Principale.this, Const.LANCEMENT_REQUETE, Toast.LENGTH_SHORT).show();

            new Thread(new Runnable() { public void run() {
                // Vérification que l'adresse du serveur donnée soit correcte
                Object params = aFaireHorsUI(donnees);
                aFaireEnUI(params);

            } }).start();

        }
    }

    @Override
    public Object aFaireHorsUI(Object o) {
        final String donnees[] = (String[]) o;
        boolean ok = false;

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL(donnees[SITE] + CHECK_PAGE + String.format(PARAMS_TRACK_TOKEN, donnees[TRACKER_ID], donnees[TOKEN]))).openConnection();
            try {
                switch(urlConnection.getResponseCode()){
                    case HttpURLConnection.HTTP_OK:
                        ok = true;
                        break;

                    case HttpURLConnection.HTTP_FORBIDDEN:

                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        runOnUiThread(new Runnable() { public void run() {Toast.makeText(Principale.this, Const.ECHEC_AUTHENTIFICATION, Toast.LENGTH_LONG).show();}});
                        break;

                    default:
                        runOnUiThread(new Runnable() { public void run() {Toast.makeText(Principale.this, Const.ECHEC_HTTP, Toast.LENGTH_LONG).show();}});
                        break;
                }

            }finally{
                urlConnection.disconnect();
            }

        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() { public void run() {Toast.makeText(Principale.this, Const.ECHEC_HTTP, Toast.LENGTH_LONG).show();}});
        }

        requeteEnCours = false;
        return (ok)? donnees:null;
    }

    @Override
    public void aFaireEnUI(Object o) {
        if (o != null) {
            final String donnees[] = (String[]) o;
            runOnUiThread(new Runnable() { public void run() {

                sharedPref.edit()
                        .putString(Const.PREF_SITE, donnees[SITE])
                        .putString(Const.PREF_TOKEN, donnees[TOKEN])
                        .putString(Const.PREF_TRACKER, donnees[TRACKER_ID])
                        .apply();

                startActivity(new Intent(Principale.this, Track.class).putExtra(Const.DONNEES, donnees));

            }});
        }
    }
}
