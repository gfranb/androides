package com.example.roulette

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.roulette.Model.FirebaseApiService
import com.example.roulette.Model.User
import com.example.roulette.databinding.FragmentClasificacionBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClasificacionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClasificacionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentClasificacionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 9001
    private lateinit var database: DatabaseReference
    private val listaUsuarios: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentClasificacionBinding.inflate(inflater, container, false)

        val context = requireContext()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        if( binding.siginGoogle.visibility == View.VISIBLE ){
            // Volver a iniciar sesion y refrescar el layout
            binding.siginGoogle.setOnClickListener {

                lifecycleScope.launch(Dispatchers.IO) {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }

            }
        }

        if( FirebaseAuth.getInstance().currentUser != null){

            lifecycleScope.launch(Dispatchers.IO){
                obtenerTopJugadores()
                Log.e("JUGADORES",listaUsuarios.toString())
                //Log.e("JUGADORES",jugadores.toString())
                
                // Nos importa de esta lista EMAIL Y PUNTOS
                val listaOrdenada = listaUsuarios.sortedByDescending { it.puntos }
            }

        }

        return binding.root

    }

    private suspend fun obtenerTopJugadores() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://androides-94b4a-default-rtdb.europe-west1.firebasedatabase.app/") // Reemplaza "tu_firebase_project_url" con la URL de tu proyecto Firebase
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(FirebaseApiService::class.java)

        val response = service.getJugadores()

        if (response.isSuccessful) {
            val gson = Gson()
            val responseBody = response.body()

            responseBody?.forEach {(userId,jugador) ->

                val email = jugador.correo
                val apuestas = jugador.apuestasGanadas
                val puntos = jugador.puntos

                val nuevoJugador = User(apuestas,email,puntos)

                listaUsuarios.add(nuevoJugador)

            }

        } else {
            // Manejar el error de la respuesta no exitosa
            throw Exception("Error al obtener los datos de Firebase: ${response.code()}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(FirebaseAuth.getInstance().currentUser == null) {
            binding.signout.visibility = View.INVISIBLE
            binding.siginGoogle.visibility = View.VISIBLE
        }else{
            binding.signout.visibility = View.VISIBLE
            binding.siginGoogle.visibility = View.INVISIBLE
        }

        if( binding.signout.visibility == View.VISIBLE){
            binding.signout.setOnClickListener{
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Cierre de sesion exitoso", Toast.LENGTH_SHORT).show()
                binding.siginGoogle.visibility = View.VISIBLE
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Obtener la cuenta de Google
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(context, "Error al iniciar sesión con Google: " + e.statusCode, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

        val activity = requireActivity()
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión con Google fue exitoso, el usuario está autenticado
                    val user = firebaseAuth.currentUser
                    // Aquí puedes realizar las acciones correspondientes después de iniciar sesión
                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    // Mostrar tabla y boton logOut
                    binding.signout.visibility = View.VISIBLE
                    binding.siginGoogle.visibility = View.INVISIBLE
                } else {
                    // Si el inicio de sesión con Google falla, muestra un mensaje de error
                    Toast.makeText(context, "Error al iniciar sesión con Google 2", Toast.LENGTH_SHORT).show()
                }
            }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClasificacionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClasificacionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}


