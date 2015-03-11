package as.swarmapp.lighttracker;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by asmodeeus on 09/03/15.
 */
public final class Const {
    public static final String PREFIXE_APP = "as.swarmapp.lighttracker.";

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE); // timestamp selon le format de Haggis. Locale.FRANCE sert à écrire les chiffres en ASCII


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
    public static final String OK_CHANGEMENTS = "Changes have been saved.   ";

    // Communication inter-activités
    public static final String DONNEES = "adresse+token";
    public static final String DIFFUSION_GENERALE= PREFIXE_APP + "BROADCAST";

        // Interaction avec le service ServiceLocalisationPOST
    public static final String ACTION           = PREFIXE_APP + "action";
    public static final String ACTION_START     = PREFIXE_APP + "action.START";
    public static final String ACTION_STOP      = PREFIXE_APP + "action.STOP";
    public static final String EXTRA_ADRESSE    = PREFIXE_APP + "extra.ADRESSE";
    public static final String EXTRA_TRACKER    = PREFIXE_APP + "extra.TRACKER";
    public static final String EXTRA_TOKEN      = PREFIXE_APP + "extra.TOKEN";

    // Préférences
    public static final String PREFERENCES      = PREFIXE_APP + "preferences";
    public static final String PREF_SITE        = PREFIXE_APP + "site";
    public static final String PREF_TOKEN       = PREFIXE_APP + "token";
    public static final String PREF_TRACKER     = PREFIXE_APP + "tracker_id";
    public static final String PREF_ADRESSE_POST= PREFIXE_APP + "adresse_POST";

    // Variables par défaut
    public static final String DEF_ADRESSE      = "/listeandroid";
}
