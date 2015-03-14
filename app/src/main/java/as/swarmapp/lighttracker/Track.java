package as.swarmapp.lighttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;


public class Track extends ActionBarActivity {

    private String adresse = "";
    private String token = "";
    private long tracker_id = -1;
    private SharedPreferences sharedPref;
    private Switch sTrack;

    private CompoundButton.OnCheckedChangeListener OCCLtrack = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // The toggle is enabled
                demarrerTracking();

            } else {
                // The toggle is disabled
                arreterTracking();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        // Récupération des SharedPreferences (qui donnent les données pré-remplies)
        sharedPref = getSharedPreferences(Const.PREFERENCES, Context.MODE_PRIVATE);
        sTrack = (Switch) findViewById(R.id.Strack);
        sTrack.setOnCheckedChangeListener(OCCLtrack);

        if(savedInstanceState==null){
            // Lancement de l'activité suite à une Intention
            String donnees[] = getIntent().getStringArrayExtra(Const.DONNEES);

            if (donnees != null && donnees.length > 2) {
                // Récupération des données de l'Intention
                adresse = donnees[Principale.SITE] + sharedPref.getString(Const.PREF_ADRESSE_POST, Const.DEF_ADRESSE);
                token = donnees[Principale.TOKEN];
                tracker_id = Long.valueOf(donnees[Principale.TRACKER_ID]);

                // Lancement du tracking
                demarrerTracking();

            } else {
                new Exception("Cette configuration n'est pas censée arriver").printStackTrace();
                finish();
            }

        }else{
            // Restauration de l'état antérieur de l'activité
            sTrack.setChecked(savedInstanceState.getBoolean(Const.BUN_IS_COCHE));
            adresse     = savedInstanceState.getString(Const.BUN_ADRESSE);
            token       = savedInstanceState.getString(Const.BUN_TOKEN);
            tracker_id  = savedInstanceState.getLong(Const.BUN_TRACKER);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putBoolean(Const.BUN_IS_COCHE, sTrack.isChecked());
        outState.putString(Const.BUN_ADRESSE, adresse);
        outState.putString(Const.BUN_TOKEN, token);
        outState.putLong(Const.BUN_TRACKER, tracker_id);
    }

    @Override
    public void onBackPressed() {
        arreterTracking();

        // Otherwise defer to system default behavior.
        super.onBackPressed();
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

    private void demarrerTracking(){
        new Thread(){ public void run(){
            startService(
                    new Intent(Track.this.getApplicationContext(), ServiceLocalisationPOST.class).setAction(Const.ACTION_START)
                            .putExtra(Const.EXTRA_ADRESSE, adresse)
                            .putExtra(Const.EXTRA_TOKEN, token)
                            .putExtra(Const.EXTRA_TRACKER, tracker_id)
            );
        } }.start();

    }

    private void arreterTracking() {
        //LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.DIFFUSION_GENERALE).putExtra(Const.ACTION, Const.ACTION_STOP));
        ServiceLocalisationPOST.getInstance().stopTracking();
    }
}
