package mateo.amaya.trabajocrudparabryan

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import mateo.amaya.trabajocrudparabryan.databinding.ActivityRegisterBinding
import android.content.Intent
import android.text.InputType
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.*
import modelo.ClaseConexion
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class activityRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1- Mando a llamar a todos los elementos de la vista
        val imgAtrasregistrarse = findViewById<ImageView>(R.id.imgAtrasregistrarse)
        val etNombreUsuario = findViewById<EditText>(R.id.etNombreUsuario)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etClaveUsuario = findViewById<EditText>(R.id.etClaveUsuario)
        val etConfirmarClaveUsuario = findViewById<EditText>(R.id.etConfirmarClaveUsuario)
        val imgVerPassword = findViewById<ImageView>(R.id.imgVerPassword)
        val imgVerConfirmacionPassword = findViewById<ImageView>(R.id.imgVerConfirmacionPassword)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)
        val btnRegresarLogin = findViewById<Button>(R.id.btnRegresarLogin)

        //2- Programar los botones
        btnCrearCuenta.setOnClickListener {
            val nombreUsuario = etNombreUsuario.text.toString()
            val correo = etCorreo.text.toString()
            val claveUsuario = etClaveUsuario.text.toString()
            val confirmarClaveUsuario = etConfirmarClaveUsuario.text.toString()

            if (nombreUsuario.isEmpty() || correo.isEmpty() || claveUsuario.isEmpty() || confirmarClaveUsuario.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else if (claveUsuario != confirmarClaveUsuario) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val resultado = registerUser(nombreUsuario, correo, claveUsuario)
                    withContext(Dispatchers.Main) {
                        if (resultado) {
                            Toast.makeText(this@activityRegister, "Usuario creado", Toast.LENGTH_SHORT).show()
                            etNombreUsuario.setText("")
                            etCorreo.setText("")
                            etClaveUsuario.setText("")
                            etConfirmarClaveUsuario.setText("")
                        } else {
                            Toast.makeText(this@activityRegister, "Error en el registro", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Programar los botones para mostrar u ocultar la contraseña
        imgVerPassword.setOnClickListener {
            if (etClaveUsuario.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                etClaveUsuario.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etClaveUsuario.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        imgVerConfirmacionPassword.setOnClickListener {
            if (etConfirmarClaveUsuario.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                etConfirmarClaveUsuario.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etConfirmarClaveUsuario.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        // Al darle clic a la flechita de arriba - Regresar al Login
        imgAtrasregistrarse.setOnClickListener {
            val pantallaLogin = Intent(this, activityLogeo::class.java)
            startActivity(pantallaLogin)
        }
        // Al darle clic al botón que ya tengo una cuenta - Regresar al Login
        btnRegresarLogin.setOnClickListener {
            val pantallaLogin = Intent(this, activityLogeo::class.java)
            startActivity(pantallaLogin)
        }
    }

    private fun registerUser(nombreUsuario: String, correo: String, claveUsuario: String): Boolean {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null

        return try {
            connection = ClaseConexion().cadenaConexion()
            if (connection == null) {
                println("Error: No se pudo establecer la conexión a la base de datos")
                return false
            }

            val sql = "INSERT INTO tbUsuariosProyectoBryan (nombreUsuario, correo, claveUsuario) VALUES (?, ?, ?)"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, nombreUsuario)
            preparedStatement.setString(2, correo)
            preparedStatement.setString(3, claveUsuario)

            val rowsInserted = preparedStatement.executeUpdate()
            rowsInserted > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }

}