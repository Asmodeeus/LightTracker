package as.swarmapp.lighttracker;

import android.content.Intent;
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

    private CompoundButton.OnCheckedChangeListener OCCLtrack = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // The toggle is enabled
                reprendreTracking();

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
        String donnees[] = getIntent().getStringArrayExtra(Const.DONNEES);

        if (donnees != null && donnees.length > 2) {
            adresse = donnees[Principale.SITE];
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

        Log.w("demarrerTracking", ".");
        /*
        Intent mIdS = new Intent(this, calisationPOST.class);
        mIdS.setAction(Const.ACTION_START)
                .putExtra(Const.EXTRA_ADRESSE, adresse)
                .putExtra(Const.EXTRA_TOKEN, token)
                .putExtra(Const.EXTRA_TRACKER, tracker_id);
        startService(mIdS);

        //*/
        new Thread(){ public void run(){
                startService(
                        new Intent(Track.this.getApplicationContext(), ServiceLocalisationPOST.class).setAction(Const.ACTION_START)
                                .putExtra(Const.EXTRA_ADRESSE, adresse)
                                .putExtra(Const.EXTRA_TOKEN, token)
                                .putExtra(Const.EXTRA_TRACKER, tracker_id)
                );
        } }.start();

    }

    private void reprendreTracking(){
        Log.w("reprendreTracking", ".");

    }

    private void arreterTracking() {
        Log.w("arreterTracking", ".");
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.DIFFUSION_GENERALE).putExtra(Const.ACTION, Const.ACTION_STOP));

    }
}
