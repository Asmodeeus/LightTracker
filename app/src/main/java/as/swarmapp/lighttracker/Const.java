package as.swarmapp.lighttracker;

/**
 * Created by asmodeeus on 09/03/15.
 */
public final class Const {
    // Variables communes au serveur
    public static final String TRACKER_ID   = "tracker_id";
    public static final String TOKEN        = "token";

    // Variables liées au serveur
    public static final String REGEX_SITE = "[a-zA-Z0-9][-a-zA-Z0-9]*(\\.[-a-zA-Z0-9]+)+(:[1-9][0-9]*)?"; // Vaguement fool-proof ...
    public static final String REGEX_TOKEN = "[a-f0-9]+";
    public static final String REGEX_TRACKER_ID = "[0-9]+";

    // Communication avec l'utilisateur
    public static final String REQUETE_EN_COURS = "A request is pending, please wait for it to terminate.";
    public static final String LANCEMENT_REQUETE = "Address checking : pending";
    public static final String ECHEC_HTTP = "Could not establish connection to server, please check the address";
    public static final String ECHEC_AUTHENTIFICATION = "Tracker id/Token were not accepted by server, please check them.";
    public static final String INVALID_ = "\"%s\" is invalid.";

    // Communication inter-activités
    public static final String DONNEES = "adresse+token";

    // Préférences
    public static final String PREFIXE_PREF = "as.swarmapp.lighttracker.";
    public static final String PREFERENCES  = PREFIXE_PREF + "preferences";
    public static final String PREF_SITE    = PREFIXE_PREF + "site";
    public static final String PREF_TOKEN   = PREFIXE_PREF + "token";
    public static final String PREF_TRACKER = PREFIXE_PREF + "tracker_id";
}
