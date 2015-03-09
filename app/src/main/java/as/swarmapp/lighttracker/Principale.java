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
import android.widget.Toast;

import java.util.List;


public class Principale extends ActionBarActivity {
    private SharedPreferences 		sharedPref;
    private EditText eSite;
    private EditText eToken;
    private String site_debug = "http://t-viravau.duckdns.org:8000"; //FIXME : à retirer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principale);
        sharedPref 	= getSharedPreferences(Const.PREFERENCES, Context.MODE_PRIVATE);


        eSite = (EditText) findViewById(R.id.Esite);
        eToken = (EditText) findViewById(R.id.Etoken);
        eSite.setText(sharedPref.getString(Const.PREF_SITE, ""+site_debug));
        eToken.setText(sharedPref.getString(Const.PREF_TOKEN, ""));

        ((Button) findViewById(R.id.Bgo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String donnees[] = donneesAreOk();
                if (donnees != null) {
                    sharedPref.edit()
                            .putString(Const.PREF_SITE, donnees[0])
                            .putString(Const.PREF_TOKEN, donnees[1])
                            .apply();

                    startActivity(new Intent(Principale.this, Track.class).putExtra(Const.DONNEES, donnees));
                }
            }
        });
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
}
