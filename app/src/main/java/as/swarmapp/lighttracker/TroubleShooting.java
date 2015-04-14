package as.swarmapp.lighttracker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import as.swarmapp.lighttracker.BaseDeDonnees.DAOPosition;


public class TroubleShooting extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trouble_shooting);


        List<String> lesEvenements = (DAOPosition.getInstance(TroubleShooting.this).listeEvenements());
        lesEvenements.add("coucou");
        lesEvenements.add("coucou2");

        if (lesEvenements.isEmpty()) {
            // S'il n'y a aucun évènement dans la BDD, on return null

        }else {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(TroubleShooting.this, android.R.layout.simple_spinner_item, lesEvenements);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ((ListView) findViewById(R.id.LVimprevus)).setAdapter((ArrayAdapter<String>) dataAdapter);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trouble_shooting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.RAZimprevus) {
            Imprevus.reset();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
