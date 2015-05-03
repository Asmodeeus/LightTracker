package as.swarmapp.lighttracker;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/** Dialogue permettant à l'utilisateur de répondre oui ou non à une question.<br>
 * Cette classe se comporte comme une usine, elle n'est pas les boites de dialogues mais elle les génère.<br>
 * Utilisation :
 * <li> Pour bannir un joueur
 * <li> Pour mettre fin à la partie
 *
 * @author thibault
 */
public class FragmentDialogue extends DialogFragment {
    private int 		typeDialogue;
    private String donnees;
    /* invisible correspond à des donées que l'on stocke dans la fenetre. On aurait peut-être pu utiliser les View.(set/get)Tag */
    private TextView invisible;
    private TextView Tmessage;
    private Button B1;
    private Button B2;
    private Button B3;
    /** Callback activé lors du clic sur un des boutons de choix possibles */
    private OnClickListener clicBouton = new OnClickListener(){
        @Override
        public void onClick(View v) {
            FragmentDialogueListener parent = (FragmentDialogueListener) getActivity();
            switch(v.getId()){

                case R.id.B1:
                    parent.choixDialogue(Const.BtnFICHIER, invisible.getText().toString());
                    break;

                case R.id.B2:
                    parent.choixDialogue(Const.BtnPOST, invisible.getText().toString());
                    break;

                case R.id.B3:
                    parent.choixDialogue(Const.BtnMARQUER, invisible.getText().toString());
                    break;

                default:
                    break;

            }
            // On ferme la boite de dialogue courante
            dismiss();
        }
    };

    /**	Interface : tout objet qui a besoin de manipuler les FragmentDialogue doit implanter cette méthode.<br>
     * C'est celle qui est appelée dans le OnclickListener "clicBouton".
     */
    public interface FragmentDialogueListener {
        /**
         * Callback appelé lorsque l'utilisateur clique sur l'un des deux boutons proposés
         * @param bouton	: le numéro du bouton : 1 pour le bouton de gauche, 2 pour le bouton de droite
         * @param donnees	: des données cachées dans la boite de dialogue, à récupérer
         */
        void choixDialogue(int bouton, String donnees);
    }

    public FragmentDialogue() {
    }

    /** Méthode de création du dialogue.<br>
     * Il s'agit de paramétrer correctement la boite de dialogue, notamment en ce qui concerne les données cachées.<br>
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null){
            typeDialogue = args.getInt(Const.BUN_TYPE_FRAG, -1);
            donnees = args.getString(Const.BUN_DONNEES, "");

        }else if (savedInstanceState != null){
            typeDialogue = savedInstanceState.getInt(Const.BUN_TYPE_FRAG, -1);
            donnees = savedInstanceState.getString(Const.BUN_DONNEES, "");

        }else{
            throw new AssertionError("Fragment instancié sans paramètres");
        }


        // Récupération des vues
        View view 	= inflater.inflate(R.layout.fragment_dialogue, container);
        invisible	= (TextView)	view.findViewById(R.id.donnees_invisibles);
        Tmessage	= (TextView)	view.findViewById(R.id.Tmessage);
        B1 			= (Button) 		view.findViewById(R.id.B1);
        B2 			= (Button) 		view.findViewById(R.id.B2);
        B3 			= (Button) 		view.findViewById(R.id.B3);
        B1.setOnClickListener(clicBouton);
        B2.setOnClickListener(clicBouton);
        B3.setOnClickListener(clicBouton);
        B1.setText(getString(R.string.D_fichier));
        B2.setText(getString(R.string.D_POST));
        B3.setText(getString(R.string.D_MARQUER));
        invisible.setText(donnees);


        // Le joueur est un penseur qui a cliqué sur un joueur de la liste
        getDialog().setTitle(getString(R.string.DTIdump));
        Tmessage.setText(getString(R.string.DTXdump));


        // Le dialogue disparait si on clique en dehors de la boite
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

}
