package mateo.amaya.trabajocrudparabryan

import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbTicketProyectoBryan
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.UUID


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1- Llamada a todos los elementos de la vista
        val txtTitulo = findViewById<EditText>(R.id.txtTitulo)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtNombreUsuario = findViewById<EditText>(R.id.txtNombreUsuario)
        val txtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val txtTelefono = findViewById<EditText>(R.id.txtTelefono)
        val txtUbicacion = findViewById<EditText>(R.id.txtUbicacion)
        val btnSubir = findViewById<Button>(R.id.btnSubir)
        val rcvTicket = findViewById<RecyclerView>(R.id.rcvTicket)

        //agregar un layout al recycleview
        rcvTicket.layoutManager = LinearLayoutManager(this)

        //asignar un adaptador
        CoroutineScope(Dispatchers.IO).launch {
            val ticketsDB = obtenerTickets()
            withContext(Dispatchers.Main) {
                val adapter = Adaptador(ticketsDB) { ticket ->
                    eliminarTicket(ticket)
                }
                rcvTicket.adapter = adapter
            }
        }

        //función para limpiar campos de texto
        fun limpiarCampos() {
            txtTitulo.setText("")
            txtDescripcion.setText("")
            txtNombreUsuario.setText("")
            txtCorreo.setText("")
            txtTelefono.setText("")
            txtUbicacion.setText("")
        }

        //2- Programación del botón de subir
        btnSubir.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                //1-crear un objeto de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //2-creo una variable que contenga prepareStatement
                val addTicket = objConexion?.prepareStatement(
                    "insert into tbTicketProyectoBryan(uuid, tituloTicket, descripcionTicket, responsableTicket, emailContactador, telefonoContactador, ubicacionContactador) values(?, ?, ?, ?, ?, ?, ?)"
                )!!
                addTicket.setString(1, UUID.randomUUID().toString())
                addTicket.setString(2, txtTitulo.text.toString())
                addTicket.setString(3, txtDescripcion.text.toString())
                addTicket.setString(4, txtNombreUsuario.text.toString())
                addTicket.setString(5, txtCorreo.text.toString())
                addTicket.setString(6, txtTelefono.text.toString())
                addTicket.setString(7, txtUbicacion.text.toString())
                addTicket.executeUpdate()

                val nuevosProductos = obtenerTickets()

                withContext(Dispatchers.Main) {
                    (rcvTicket.adapter as? Adaptador)?.actualizarLista(nuevosProductos)
                    limpiarCampos()
                }
            }
        }
    }

    private fun obtenerTickets(): List<tbTicketProyectoBryan> {
        val listaTickets = mutableListOf<tbTicketProyectoBryan>()
        var objConexion: Connection? = null
        var resultSet: ResultSet? = null

        try {
            objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            resultSet = statement?.executeQuery("SELECT * FROM tbTicketProyectoBryan")

            if (resultSet != null) {
                while (resultSet.next()) {
                    val uuid = resultSet.getString("uuid")
                    val tituloTicket = resultSet.getString("tituloTicket")
                    val descripcionTicket = resultSet.getString("descripcionTicket")
                    val nombreUsuario = resultSet.getString("responsableTicket")
                    val emailContactador = resultSet.getString("emailContactador")
                    val telefonoContactador = resultSet.getString("telefonoContactador")
                    val ubicacionContactador = resultSet.getString("ubicacionContactador")

                    val valoresJuntos = tbTicketProyectoBryan(
                        uuid,
                        tituloTicket,
                        descripcionTicket,
                        nombreUsuario,
                        emailContactador,
                        telefonoContactador,
                        ubicacionContactador
                    )

                    listaTickets.add(valoresJuntos)
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                resultSet?.close()
                objConexion?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return listaTickets
    }

    private fun eliminarTicket(ticket: tbTicketProyectoBryan) {
        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().cadenaConexion()
            val deleteTicket = objConexion?.prepareStatement("DELETE FROM tbTicketProyectoBryan WHERE uuid = ?")!!
            deleteTicket.setString(1, ticket.uuid)
            deleteTicket.executeUpdate()

            val nuevosProductos = obtenerTickets()

            withContext(Dispatchers.Main) {
                (findViewById<RecyclerView>(R.id.rcvTicket).adapter as? Adaptador)?.actualizarLista(nuevosProductos)
            }
        }
    }
}