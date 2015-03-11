package as.swarmapp.lighttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;


public class Track extends ActionBarActivity {

    private String adresse = "";
    private String token = "";
    private long tracker_id = -1;
    private SharedPreferences sharedPref;

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
        sharedPref 	= getSharedPreferences(Const.PREFERENCES, Context.MODE_PRIVATE);
        String donnees[] = getIntent().getStringArrayExtra(Const.DONNEES);

        if (donnees != null && donnees.length > 2) {
            adresse = donnees[Principale.SITE]+sharedPref.getString(Const.PREF_ADRESSE_POST, Const.DEF_ADRESSE);
            token = donnees[Principale.TOKEN];
            tracker_id = Long.valueOf(donnees[Principale.TRACKER_ID]);

            Switch sTrack = (Switch) findViewById(R.id.Strack);
            sTrack.setOnCheckedChangeListener(OCCLtrack);
            demarrerTracking();

        }else{
            new Exception("Cette configuration n'est pas censée arriver").printStackTrace();
            finish();
        }
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
        Log.w("arreterTracking", ".");
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.DIFFUSION_GENERALE).putExtra(Const.ACTION, Const.ACTION_STOP));
        ServiceLocalisationPOST.getInstance().stopTracking();
    }
}
