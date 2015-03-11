package as.swarmapp.lighttracker;

/**
 * Created by asmodeeus on 11/03/15.
 */
public class Imprevus {

    private final static long UN = 0b1;
    public final static long INCONNUE = UN << 1;
    public final static long E_BAD_REQUEST = UN << 2;
    public final static long E_SERVICE_UNAVAILABLE = UN << 3;

    private static long erreurs = 0;
    private static long avert = 0;

    private Imprevus(){
        throw new AssertionError("Imprevus ne doit pas être instancié");
    }

    public static void rapporterErreur(long err){
        erreurs|=err;
    }

    public static void supprimerErreur(long err){
        erreurs&=(~err);
    }

    public static void rapporterAvertissement(long war){
        avert |=war;
    }

    public static void supprimerAvertissement(long war){
        avert &=(~war);
    }

    public static long getErreurs() {
        return erreurs;
    }

    public static long getAvertissements() {
        return avert;
    }

    public static void reset(){
        erreurs = 0;
        avert = 0;
    }
}
