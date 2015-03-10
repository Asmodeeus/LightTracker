package as.swarmapp.lighttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class Principale extends ActionBarActivity implements GestionHorsUI {
    private static String   CHECK_PHRASE    = "Hello ! Everything seems fine around here ;)";
    private static String   CHECK_PAGE      = "/what";
    private static int      SITE            = 0;
    private static int      TOKEN           = 1;
    private static String   site_debug      = "http://t-viravau.duckdns.org:18001"; //FIXME : à retirer
    private static String   token_debug     = "249737703537f0de5c007e30f7b009b4"; //FIXME : à retirer

    // Sémaphore interdisant d'executer plusieurs requêtes en même temps
    private boolean         requeteEnCours  = false;
    private SharedPreferences 		sharedPref;
    private EditText eSite;
    private EditText eToken;

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


        eSite = (EditText) findViewById(R.id.Esite);
        eToken = (EditText) findViewById(R.id.Etoken);
        eSite.setText(sharedPref.getString(Const.PREF_SITE, "" +site_debug));
        eToken.setText(sharedPref.getString(Const.PREF_TOKEN, "" +token_debug));

        ((Button) findViewById(R.id.Bgo)).setOnClickListener(OCLBgo);
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
        String prefixe = "http://";

        if (site.startsWith("http://")){
            site = site.substring(7);

        }else if (site.startsWith("https://")){
            site = site.substring(8);
            prefixe = "https://";
        }

        if (site.matches(Const.REGEX_SITE)){
            if (token.matches(Const.REGEX_TOKEN)){
                return new String[]{prefixe + site, token};

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
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL(donnees[SITE] + CHECK_PAGE)).openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                ok = (Utiles.streamToString(in).compareTo(CHECK_PHRASE)==0);
            }finally{
                urlConnection.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        requeteEnCours = false;

        if (ok)
            return donnees;
        else
            return null;
    }

    @Override
    public void aFaireEnUI(Object o) {
        if (o != null) {
            final String donnees[] = (String[]) o;
            runOnUiThread(new Runnable() { public void run() {

                sharedPref.edit()
                        .putString(Const.PREF_SITE, donnees[SITE])
                        .putString(Const.PREF_TOKEN, donnees[TOKEN])
                        // TODO  ajouter le tracker_id
                        .apply();

                startActivity(new Intent(Principale.this, Track.class).putExtra(Const.DONNEES, donnees));

            }});
        }else{
            runOnUiThread(new Runnable() { public void run() {Toast.makeText(Principale.this, Const.ECHEC_REQUETE, Toast.LENGTH_LONG).show();}});
        }
    }
}
