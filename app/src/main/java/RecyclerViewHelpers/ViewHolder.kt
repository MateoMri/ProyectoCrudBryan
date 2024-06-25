package RecyclerViewHelpers

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mateo.amaya.trabajocrudparabryan.R

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val txtNombreCard: TextView = itemView.findViewById(R.id.txtNombreCard)
    val txtDescripcionCard: TextView = itemView.findViewById(R.id.txtDescripcionCard)
    val txtUsuarioCard: TextView = itemView.findViewById(R.id.txtUsuarioCard)
    val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
}