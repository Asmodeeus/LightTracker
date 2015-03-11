package as.swarmapp.lighttracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Reglages extends ActionBarActivity {
    private SharedPreferences   sharedPref;
    private EditText            eAdressePOST;

    private View.OnClickListener OCLvalider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharedPref.edit()
                      .putString(Const.PREF_ADRESSE_POST, eAdressePOST.getText().toString())
                      .apply();
            Toast.makeText(Reglages.this, Const.OK_CHANGEMENTS, Toast.LENGTH_SHORT).show();
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reglages);

        sharedPref = getSharedPreferences(Const.PREFERENCES, Context.MODE_PRIVATE);
        eAdressePOST = (EditText) findViewById(R.id.EadressePOST);
        eAdressePOST.setText(sharedPref.getString(Const.PREF_ADRESSE_POST, Const.DEF_ADRESSE));
        findViewById(R.id.Bvalider).setOnClickListener(OCLvalider);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reglages, menu);
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
}
