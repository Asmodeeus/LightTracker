package as.swarmapp.lighttracker;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import as.swarmapp.lighttracker.BaseDeDonnees.Position;

/**
 * Created by asmodeeus on 09/03/15.
 */
public final class Utiles {


    public static String streamToString(InputStream in){
        int n = 0;
        StringBuffer sb = new StringBuffer();
        try{
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            while ((n = isr.read()) != -1) {
                sb.append((char)n);
            }
            in.close();

        }catch (IOException e){
            e.printStackTrace();
            return "";

        }
        return sb.toString();
    }

    public static List<String> listePositionToString(List<Position> lp){
        List<String> toR = new ArrayList<String>();
        for (Position p:lp){
            toR.add(p.toString());
        }
        return toR;
    }

    public static boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }


    public static File getUnFichierDeDump() throws FileNotFoundException   {
        if (isExternalStorageWritable()) {
            //  Récupère le dossier à l'emplacement par défaut du dossier "documents"
            File dossierDumps = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Const.DOSSIER_DUMPS);

            // Créer le dossier s'il n'existe pas
            if (!dossierDumps.exists()) {
                if (!dossierDumps.mkdirs()) {
                    throw new FileNotFoundException(Const.ERR_DOSSIER_DUMP);
                }
            }
            File fichier = new File(dossierDumps.getPath() + File.separator + "dump" + Const.SDFrequetes.format(new Date()) + ".txt");


            return fichier;
        }else{
            throw new FileNotFoundException(Const.ECHEC_ACCES_SD);
        }
    }
}
