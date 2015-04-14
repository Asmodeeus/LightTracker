package as.swarmapp.lighttracker;

import java.util.List;

/**
 * Created by asmodeeus on 11/03/15.
 */
public class Imprevus {

    private final static int UN = 0b1;
    public final static long INCONNUE                   = UN << 1;
    public final static long E_BAD_REQUEST              = UN << 2;
    public final static long E_SERVICE_UNAVAILABLE      = UN << 3;
    public final static long E_API_CONNECTION_FAILED    = UN << 4;

    public final static int W_API_CONNEXION_SUSPENDUE   = UN << 1;
    public final static int W_CONNEXION_PERDUE          = UN << 2;

    private static long erreurs = 0L;
    private static int avert = 0;

    private Imprevus(){
        throw new AssertionError("Imprevus ne doit pas être instancié");
    }

    public static void rapporterErreur(long err){
        erreurs|=err;
    }

    public static void supprimerErreur(long err){
        erreurs&=(~err);
    }

    public static void rapporterAvertissement(int war){
        avert |=war;
    }

    public static void supprimerAvertissement(long war){
        avert &=(~war);
    }

    public static long getErreurs() {
        return erreurs;
    }

    public static int getAvertissements() {
        return avert;
    }

    public static void reset(){
        erreurs = 0L;
        avert = 0;
    }

    public static List<String> aficherErreurs(){

        return null;
    }
    //TODO : créer une fonction qui retourne une liste de string avec le nom de l'erreur, ou " " si pas d'erreur
}
