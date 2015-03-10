package as.swarmapp.lighttracker.BaseDeDonnees;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by asmodeeus on 10/03/15.
 */
public class ManipuleurBDD {

    public static void reCreer(SQLiteDatabase db){
        dropAll(db);
        createAll(db);
    }

    private ManipuleurBDD(){
        throw new AssertionError("Manipuleur ne doit être instancié");
    }

    private static void createAll(SQLiteDatabase db) {
        db.execSQL(DAOPosition.CREATE);
    }

    private static void dropAll(SQLiteDatabase db){
        db.execSQL(DAOPosition.DROP);

    }

}
