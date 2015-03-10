package as.swarmapp.lighttracker.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by asmodeeus on 10/03/15.
 */
public class DAOPosition extends SQLiteOpenHelper{
    // Nom de la table
    public static final int VERSION = 1;
    public static final String NOM_TABLE = "Positions";
    public static final String NOM_BDD = "as.swarmapp.lighttrack.bdd";

    // Noms des champs de la table
    public static final String KEY = "id";
    public static final String EVENEMENT    = "event";
    public static final String TRACKER_ID = "tracker_id";
    public static final String TOKEN        = "token";
    public static final String HORODATE     = "datetime";
    public static final String LATITUDE     = "latitude";
    public static final String LONGITUDE    = "longitude";
    public static final String A_ENVOYER    = "toSend";

    // Requête de création
    public static final String CREATE = "CREATE TABLE " + NOM_TABLE + " (" +
            KEY         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EVENEMENT   + " TEXT, " +
            TRACKER_ID  + " INTEGER, " +
            TOKEN       + " TEXT, " +
            HORODATE    + " TEXT, " +
            LATITUDE    + " REAL, " +
            LONGITUDE   + " REAL, " +
            A_ENVOYER   + " BOOLEAN NOT NULL CHECK ("+ A_ENVOYER +" IN (0,1)));";
    public static final String DROP = "DROP TABLE IF EXISTS " + NOM_TABLE;

    protected static SQLiteDatabase maBDD = null;
    private static DAOPosition monDAOP = null;

    public DAOPosition(Context context){
        super(context, NOM_BDD, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DAOPosition.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DAOPosition.DROP);
        onCreate(db);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        maBDD = getWritableDatabase();
        return maBDD;
    }

    public void close() {
        maBDD.close();
    }

    /*    -----------------  AJOUT          -----------------  */
    public long ajouter(Position aAjouter){
        open();

        // On met les données dans un ContentValues
        ContentValues valeurs = new ContentValues();
        valeurs.put(EVENEMENT       , aAjouter.getEvent());
        valeurs.put(TRACKER_ID      , aAjouter.getTracker_id());
        valeurs.put(TOKEN           , aAjouter.getToken());
        valeurs.put(HORODATE        , aAjouter.getDatetime());
        valeurs.put(LATITUDE        , aAjouter.getLatitude());
        valeurs.put(LONGITUDE       , aAjouter.getLongitude());
        valeurs.put(A_ENVOYER       , (aAjouter.isToSend())? 1:0);

        // On poste la requête dont on récupère la réponse
        long toR = maBDD.insert(NOM_TABLE, null, valeurs);

        close();
        return toR;
    }



    /*    -----------------  SUPPRESSION    -----------------  */
    public boolean supprimer(long id){
        open();

        // TODO
        boolean toR = false;

        close();
        return toR;
    }



    /*    -----------------  MODIFICATION   -----------------  */
    public boolean modifier(long id, Position nvT){
        open();

        // TODO
        boolean toR = false;

        close();
        return toR;
    }

    public boolean setSent(long id){
        boolean toR;
        open();

        String strSQL = "UPDATE " + NOM_TABLE + " SET " + A_ENVOYER + " = 0 WHERE " + KEY + " = " + Long.toString(id);
        Log.w("setSent", strSQL); //!

        try {
            maBDD.execSQL(strSQL);
            toR = true;
        }catch(android.database.SQLException e){
            toR = false;
        }
        close();
        return toR;
    }



    /*    -----------------  SELECTION      -----------------  */
    public Position selectionner(long id){
        open();

        Position toR = null;
        String requSQL = "select * from " + NOM_TABLE + " where "+ KEY +" = "+ Long.toString(id);
        Cursor c = maBDD.rawQuery(requSQL, null);

        if (c.moveToFirst())
            toR =  new Position(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), c.getString(4), c.getFloat(5), c.getFloat(6), (c.getLong(7)!=0) );

        close();
        return toR;
    }
}
