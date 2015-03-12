package as.swarmapp.lighttracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import as.swarmapp.lighttracker.BaseDeDonnees.DAOPosition;
import as.swarmapp.lighttracker.BaseDeDonnees.Position;


public class Recuperation extends ActionBarActivity implements GestionHorsUI {
    private Spinner sEvenement;
    private ListView lvTest;
    private CheckBox cbTous;
    private String lEvenement;
    private List<Position> lesPos;
    private boolean écritureEnCours;
    private AdapterView.OnItemSelectedListener selectionEvenement = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //Toast.makeText(Recuperation.this, "onItemSelected", Toast.LENGTH_SHORT).show();
            lEvenement = parent.getItemAtPosition(position).toString();

            new Thread(new Runnable() { public void run() {

                lesPos = DAOPosition.getInstance(Recuperation.this).listePosition(lEvenement, cbTous.isChecked());
                if (!lesPos.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            lvTest.setAdapter(new AdaptateurListeSimple(Recuperation.this, Utiles.listePositionToListeString(lesPos)));
                        }
                    });
                }

            } }).start();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Toast.makeText(Recuperation.this, "onNothingSelected", Toast.LENGTH_SHORT).show();
        }
    };
    private View.OnClickListener OCLcbTous = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(new Runnable() { public void run() {

                lesPos = DAOPosition.getInstance(Recuperation.this).listePosition(lEvenement, cbTous.isChecked());
                if (!lesPos.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            lvTest.setAdapter(new AdaptateurListeSimple(Recuperation.this, Utiles.listePositionToListeString(lesPos)));
                        }
                    });
                }

            } }).start();
        }
    };
    private View.OnClickListener OCLdump = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (lesPos==null || lesPos.isEmpty()){
                Utiles.toastLong(Recuperation.this, Const.ECHEC);
                return;
            }
            new Thread(new Runnable() { public void run() {
                if (!écritureEnCours) {
                    écritureEnCours = true;

                    try {
                        FileOutputStream os = new FileOutputStream(Utiles.getUnFichierDeDump());
                        os.write(Utiles.listePositionToStringForDump(lesPos).getBytes());
                        os.close();

                    } catch (FileNotFoundException e) {
                        Utiles.toastLong(Recuperation.this, e.getMessage());

                    } catch (IOException e) {
                        Utiles.toastLong(Recuperation.this, Const.ECHEC_IO);

                    }finally{
                        écritureEnCours = false;

                    }
                }else{
                    Utiles.toastLong(Recuperation.this, Const.DUMP_PENDING);

                }
            } }).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperation);

        lvTest = (ListView) findViewById(R.id.LVpositions);
        sEvenement = (Spinner)findViewById(R.id.Sevenement);
        cbTous = (CheckBox) findViewById(R.id.CBtous);
        cbTous.setOnClickListener(OCLcbTous);
        (findViewById(R.id.Bdump)).setOnClickListener(OCLdump);
        MAJaffichage(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recuperation, menu);
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

    @Override
    public void MAJaffichage(final Object o) {
        new Thread(new Runnable() { public void run() {
            // aFaireHorsUI nous dit si l'on doit afficher le layout normal ou passer directement à une autre activité
            Object params = aFaireHorsUI(o);
            aFaireEnUI(params);

        } }).start();
    }

    @Override
    public Object aFaireHorsUI(Object o) {
        List<String> lesEvenements = (DAOPosition.getInstance(Recuperation.this).listeEvenements(cbTous.isChecked()));
        if (lesEvenements.isEmpty()) {
            // S'il n'y a aucun évènement dans la BDD, on return null
            return null;

        }else {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Recuperation.this, android.R.layout.simple_spinner_item, lesEvenements);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            return dataAdapter;
        }
    }

    @Override
    public void aFaireEnUI(final Object lAdapteur) {

        runOnUiThread(new Runnable() { public void run() {

            if (lAdapteur == null) {
                // Aucun évènement n'est dans la base de données, on charge l'activité d'ajout d'un évènement
                lvTest.setVisibility(View.INVISIBLE);

            }else{
                // Il y a des évènements dans la BDD, l'affichage est celui auquel on s'attend
                sEvenement.setAdapter((ArrayAdapter<String>) lAdapteur);
                sEvenement.setOnItemSelectedListener(selectionEvenement);

            }

        }});
    }
}
