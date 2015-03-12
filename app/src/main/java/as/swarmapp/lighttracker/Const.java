package as.swarmapp.lighttracker;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by asmodeeus on 09/03/15.
 */
public final class Const {
    public static final String PREFIXE_APP = "as.swarmapp.lighttracker.";

    public static SimpleDateFormat SDFrequetes = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE); // timestamp selon le format de Haggis. Locale.FRANCE sert à écrire les chiffres en ASCII
    public static SimpleDateFormat SDFbdd      = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE); // timestamp selon le format de Haggis. Locale.FRANCE sert à écrire les chiffres en ASCII

    // Variables courantes
    public static final String TOKEN        = "token";
    public static final String TRACKER_ID   = "tracker_id";
    public static final String ADRESSE      = "site";

    // Variables liées au serveur
    public static final String REGEX_SITE = "[a-zA-Z0-9][-a-zA-Z0-9]*(\\.[-a-zA-Z0-9]+)+(:[1-9][0-9]*)?"; // Vaguement fool-proof ... le http(s):// a été retiré avant le test et est rajouté après
    public static final String REGEX_TOKEN = "[a-f0-9]+";
    public static final String REGEX_TRACKER_ID = "[0-9]+";

    // Communication avec l'utilisateur
    public static final String ERREUR               = "An error occured : ";
    public static final String REQUETE_EN_COURS     = "A request is pending, please wait for it to terminate.";
    public static final String LANCEMENT_REQUETE    = "Address checking : pending";
    public static final String ECHEC_HTTP           = "Could not establish connection to server, please check the address";
    public static final String ECHEC_AUTH           = "Tracker id/Token were not accepted by server, please check them.";
    public static final String INVALID_             = "\"%s\" is invalid.";
    public static final String OK_CHANGEMENTS       = "Changes have been saved.";
    public static final String DUMP_PENDING         = "Still processing dumping ...";
    public static final String ERR_DOSSIER_DUMP     = ERREUR + "dump directory cannot be created.";
    public static final String ECHEC_IO             = ERREUR + "file cannot be written";
    public static final String ECHEC_ACCES_SD       = "Please check that SD card is mounted";

    // Sauvegardes en bundle
    public static final String PREFIXE_BUNDLE   = PREFIXE_APP + "bundle.";
    public static final String BUN_IS_COCHE     = PREFIXE_BUNDLE + "isChecked";
    public static final String BUN_ADRESSE      = PREFIXE_BUNDLE + ADRESSE;
    public static final String BUN_TOKEN        = PREFIXE_BUNDLE + TOKEN;
    public static final String BUN_TRACKER      = PREFIXE_BUNDLE + TRACKER_ID;

    // Communication inter-activités
    public static final String DONNEES          = "adresse+token";
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
    public static final String PREF_SITE        = PREFIXE_APP + ADRESSE;
    public static final String PREF_TOKEN       = PREFIXE_APP + TOKEN;
    public static final String PREF_TRACKER     = PREFIXE_APP + TRACKER_ID;
    public static final String PREF_ADRESSE_POST= PREFIXE_APP + "adresse_POST";

    // Variables par défaut
    public static final String DEF_ADRESSE      = "/listeandroid";
    public static final String DOSSIER_DUMPS    = "LightTrackerDumps";
}
