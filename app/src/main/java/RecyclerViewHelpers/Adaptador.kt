package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import mateo.amaya.trabajocrudparabryan.R
import modelo.tbTicketProyectoBryan


class Adaptador(
    var Datos: List<tbTicketProyectoBryan>,
    private val onDeleteClick: (tbTicketProyectoBryan) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    fun actualizarLista(nuevaLista: List<tbTicketProyectoBryan>) {
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Unir el ReclycleView con la card
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Controlar a la card
        val item = Datos[position]
        holder.txtNombreCard.text = item.tituloTicket
        holder.txtDescripcionCard.text = item.descripcionTicket
        holder.txtUsuarioCard.text = item.nombreUsuario

        // Manejar el evento de click del botón de eliminar
        holder.btnEliminar.setOnClickListener {
            onDeleteClick(item)
        }
    }
}