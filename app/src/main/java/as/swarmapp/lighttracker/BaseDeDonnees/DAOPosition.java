package as.swarmapp.lighttracker.BaseDeDonnees;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by asmodeeus on 10/03/15.
 */
public class DAOPosition {
    // Nom de la table
    public static final String NAME = "Positions";
    public static final String NOM_BDD = "as.swarmapp.lighttrack.bdd";

    // Noms des champs de la table
    public static final String KEY = "id";
    public static final String LATITUDE     = "latitude";
    public static final String LONGITUDE    = "longitude";
    public static final String EVENEMENT    = "event";
    public static final String TOKEN        = "token";
    public static final String TRACKER      = "tracker_id";
    public static final String HORODATE     = "datetime";
    public static final String A_ENVOYER    = "toSend";

    // Requête de création
    public static final String CREATE = "CREATE TABLE " + NAME + " (" +
            KEY         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LATITUDE    + " REAL, " +
            LONGITUDE   + " REAL, " +
            EVENEMENT   + " TEXT, " +
            TOKEN       + " TEXT, " +
            TRACKER     + " INTEGER, " +
            HORODATE    + " TEXT, " +
            A_ENVOYER   + " BOOLEAN NOT NULL CHECK ("+ A_ENVOYER +" IN (0,1)));";
    public static final String DROP = "DROP TABLE IF EXISTS " + NAME;

    protected static SQLiteDatabase maBDD = null;

    private DAOPosition(){
        throw new AssertionError("DAOEvenement ne doit pas être instancié");
    }

}
