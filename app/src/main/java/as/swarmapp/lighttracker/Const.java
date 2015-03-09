package as.swarmapp.lighttracker;

/**
 * Created by asmodeeus on 09/03/15.
 */
public final class Const {
    public static final String REGEX_SITE = "[a-zA-Z0-9][-a-zA-Z0-9]*(\\.[-a-zA-Z0-9]+)+(:[1-9][0-9]*)?"; // Vaguement fool-proof ...
    public static final String REGEX_TOKEN = "[a-f0-9]+";

    public static final String INVALID_ = "\"%s\" is invalid.";
    public static final String ERROR_WITH_ = "Error with %s : ";
    public static final String DOESN_T_MATCH = "it doesn't match the expected regular expression";
    public static final String ERROR___ = ERROR_WITH_ + "\"%s\", " + DOESN_T_MATCH + " %s";
    public static final String REQUETE_EN_COURS = "A request is pending, please wait for it to terminate.";
    public static final String LANCEMENT_REQUETE = "Address checking : pending";
    public static final String ECHEC_REQUETE = "The server could not be verified, please check the address";

    public static final String DONNEES = "adresse+token";

    public static final String PREFERENCES = "LightTracker.preferences";
    public static final String PREF_SITE = "site";
    public static final String PREF_TOKEN = "token";
}
