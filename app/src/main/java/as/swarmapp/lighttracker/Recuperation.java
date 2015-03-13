package as.swarmapp.lighttracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import as.swarmapp.lighttracker.BaseDeDonnees.DAOPosition;
import as.swarmapp.lighttracker.BaseDeDonnees.Position;


public class Recuperation extends ActionBarActivity implements GestionHorsUI, FragmentDialogue.FragmentDialogueListener {
    private Spinner sEvenement;
    private ListView lvTest;
    private TextView tRien;
    private CheckBox cbTous;
    private String lEvenement;
    private List<Position> lesPos;
    private boolean écritureEnCours;
    private Runnable rafraichirListe = new Runnable() {
        public void run() {
            if (lvTest!=null) {
                if (lesPos != null && (!lesPos.isEmpty())) {
                    tRien.setVisibility(View.GONE);
                    lvTest.setVisibility(View.VISIBLE);
                    lvTest.setAdapter(new AdaptateurListeSimple(Recuperation.this, Utiles.listePositionToListeString(lesPos)));
                } else {
                    lvTest.setVisibility(View.GONE);
                    tRien.setVisibility(View.VISIBLE);
                }
            }
        }
    };
    private Runnable miseAJourListe = new Runnable() {
        public void run() {
            lesPos = DAOPosition.getInstance(Recuperation.this).listePosition(lEvenement, cbTous.isChecked());
            runOnUiThread(rafraichirListe);
        }
    };
    private AdapterView.OnItemSelectedListener selectionEvenement = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //Toast.makeText(Recuperation.this, "onItemSelected", Toast.LENGTH_SHORT).show();
            lEvenement = parent.getItemAtPosition(position).toString();

            new Thread(miseAJourListe).start();

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(Recuperation.this, "onNothingSelected", Toast.LENGTH_SHORT).show();
        }
    };
    private View.OnClickListener OCLcbTous = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(miseAJourListe).start();
        }
    };
    private View.OnClickListener OCLdump = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Création du dialogue
            FragmentDialogue frag = new FragmentDialogue();

            // Paramétrage
            Bundle arguments = new Bundle();
            arguments.putInt(Const.BUN_TYPE_FRAG, -1);
            frag.setArguments(arguments);

            // Affichage
            frag.show( getSupportFragmentManager(), Const.TAG_DUMP);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperation);

        lvTest      = (ListView) findViewById(R.id.LVpositions);
        sEvenement  = (Spinner)findViewById(R.id.Sevenement);
        cbTous      = (CheckBox) findViewById(R.id.CBtous);
        tRien       = (TextView) findViewById(R.id.TrienAAfficher);
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
    public void choixDialogue(int bouton, String donnees) {
        if (bouton==Const.BtnFICHIER){
            if (lesPos==null || lesPos.isEmpty()){
                Utiles.toastLong(Recuperation.this, Const.ECHEC);
                return;
            }
            new Thread(new Runnable() { public void run() {
                if (!écritureEnCours) {
                    écritureEnCours = true;
                    Utiles.toastLong(Recuperation.this, Const.PROCESS_DUMP);

                    try {
                        FileOutputStream os = new FileOutputStream(Utiles.getUnFichierDeDump());
                        os.write(Utiles.listePositionToStringForDump(lesPos).getBytes());
                        os.close();
                        if (!DAOPosition.getInstance(Recuperation.this).setAllSent(lEvenement)){
                            Utiles.toastLong(Recuperation.this, Const.ECHEC_BDD);
                        }
                        new Thread(miseAJourListe).start();

                    } catch (FileNotFoundException e) {
                        Utiles.toastLong(Recuperation.this, e.getMessage());

                    } catch (Exception e) {
                        Utiles.toastLong(Recuperation.this, Const.ECHEC_IO);
                        e.printStackTrace();

                    }finally{
                        écritureEnCours = false;

                    }
                }else{
                    Utiles.toastLong(Recuperation.this, Const.DUMP_PENDING);

                }
                synchronized (this) {
                    try {
                        wait(1000); //évite le double clic
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } }).start();

        } else if(bouton==Const.BtnPOST) {
            // Effectuer un POST sur le serveur
            //TODO Effectuer un POST sur le serveur
            Log.w("TODO", "j'effectue un POST sur le serveur");
        }
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
        List<String> lesEvenements = (DAOPosition.getInstance(Recuperation.this).listeEvenements());
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
                tRien.setVisibility(View.VISIBLE);

            }else{
                // Il y a des évènements dans la BDD, l'affichage est celui auquel on s'attend
                sEvenement.setAdapter((ArrayAdapter<String>) lAdapteur);
                sEvenement.setOnItemSelectedListener(selectionEvenement);

            }
        }});
    }
}
