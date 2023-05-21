package com.example.roulette

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.roulette.databinding.ActivityStartScreenBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StartScreen : AppCompatActivity() {

    private lateinit var binding: ActivityStartScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hago el binding y lo muestro e inflo la vista.
        binding = ActivityStartScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser == null) {
            binding.siginGoogle.visibility = View.VISIBLE
        }else {
            binding.siginGoogle.visibility = View.INVISIBLE
        }

        // Configuración de inicio de sesión con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnStart.setOnClickListener(){
            val context = binding.btnStart.context
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        binding.siginGoogle.setOnClickListener(){

            lifecycleScope.launch(Dispatchers.IO) {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

        }

    }

    // Manejar el resultado de inicio de sesión con Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Obtener la cuenta de Google
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesión con Google: " + e.statusCode, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión con Google fue exitoso, el usuario está autenticado
                    val user = firebaseAuth.currentUser
                    // Aquí puedes realizar las acciones correspondientes después de iniciar sesión
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    // Iniciar la siguiente actividad:
                    val context = binding.btnStart.context
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    // Si el inicio de sesión con Google falla, muestra un mensaje de error
                    Toast.makeText(this, "Error al iniciar sesión con Google 2", Toast.LENGTH_SHORT).show()
                }
            }
    }
}