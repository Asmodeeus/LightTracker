package as.swarmapp.lighttracker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/** Adaptateur de liste.<br>
 * Les adaptateurs génèrent l'affichage de chaque ligne d'une ListView.<br>
 * La vue générée peut varier en fonction du contenu de la ligne en question.<br>
 * <br>
 * @author thibault
 */
public class AdaptateurListeSimple extends BaseAdapter {

	Context context;
	List<String> listeStr;

	/* classe qui reprend les composants du layout des vues créées (un peu inutile ici) */
	private class ViewHolder {
		private TextView texte;
	}

	public AdaptateurListeSimple(Context context, List<String> items) {
		this.context = context;
		this.listeStr = items;
	}

	@Override
	public int getCount() {    
		return listeStr.size();
	}

	@Override
	public Object getItem(int position) {
		return listeStr.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listeStr.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// On récupère l'élément affiché.
		String leTexte = ((String) getItem(position));

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;

		// Caractéristiques propres aux Adapter : on recycle les ConvertView
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.liste_1, null);
			holder = new ViewHolder();
			holder.texte = (TextView) convertView.findViewById(R.id.Tcircuit);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}         

		// On injecte les données dans le view
		holder.texte.setText(leTexte);

		return convertView;
	}

}