package com.example.roulette

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.roulette.databinding.FragmentGameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentGameBinding
    var money: Int = 1
    private var apuestas: ArrayList<Apuesta>? = null
    private var esRojo: Boolean? = null
    private var esVerde: Boolean? = null
    private var esNegro: Boolean? = null
    var importeApostadoNegro: Int = 0
    var importeApostadoRojo: Int = 0
    var importeApostadoVerde: Int = 0
    var idApuesta: Int = 0


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
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater,container,false)

        val app = Room.databaseBuilder(requireActivity().applicationContext,ApuestaDB::class.java, "apuesta").fallbackToDestructiveMigration()
            .build()

        lifecycleScope.launch(Dispatchers.IO){
            if(app.apuestaDao().getAll().isEmpty()){
                app.apuestaDao().insert(Apuesta(0,"Reset",0,100))
            }
            money = app.apuestaDao().obtenerDineroDisponible()
            println(money)

            withContext(Dispatchers.Main){
                binding.tvMoneyCount.text = money.toString() + "$"
            }
        }

        if(apuestas == null){
            apuestas = ArrayList()
        }

        binding.btnRojo.setOnClickListener(){

           if(apuestas?.isEmpty() == true){
               apuestas = ArrayList()
           }

            if( binding.etCoinsToPlay.text.toString().toInt() <= 0 || binding.etCoinsToPlay.text.toString() == "") {

                binding.etCoinsToPlay.setError("Inserte una apuesta para jugar")
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_error_background)

            }else if(money - binding.etCoinsToPlay.text.toString().toInt() >= 0){

                idApuesta++
                money -= binding.etCoinsToPlay.text.toString().toInt()
                apuestas?.add(Apuesta(0,"Rojo",binding.etCoinsToPlay.text.toString().toInt(),money))
                importeApostadoRojo += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasRojo.text = importeApostadoRojo.toString() + "$"
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_background)
                binding.etCoinsToPlay.setText("0")

            }

            println(money)

        }

        binding.btnVerde.setOnClickListener(){

            if(apuestas?.isEmpty() == true){
                apuestas = ArrayList()
            }

            if( binding.etCoinsToPlay.text.toString().toInt() <= 0 || binding.etCoinsToPlay.text.toString() == "") {

                binding.etCoinsToPlay.setError("Inserte una apuesta para jugar")
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_error_background)

            }else if(money - binding.etCoinsToPlay.text.toString().toInt() >= 0){

                idApuesta++
                money -= binding.etCoinsToPlay.text.toString().toInt()
                apuestas?.add(Apuesta(0,"Verde",binding.etCoinsToPlay.text.toString().toInt(),money))
                importeApostadoVerde += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasVerde.text = importeApostadoVerde.toString() + "$"
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_background)
                binding.etCoinsToPlay.setText("0")
            }
            println(money)
        }

        binding.btnNegro.setOnClickListener(){

            if(apuestas?.isEmpty() == true){
                apuestas = ArrayList()
            }

            if( binding.etCoinsToPlay.text.toString().toInt() <= 0 || binding.etCoinsToPlay.text.toString() == "") {

                binding.etCoinsToPlay.setError("Inserte una apuesta para jugar")
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_error_background)

                }else if (money - binding.etCoinsToPlay.text.toString().toInt() >= 0) {

                idApuesta++
                money -= binding.etCoinsToPlay.text.toString().toInt()
                apuestas?.add(Apuesta(0,"Negro", binding.etCoinsToPlay.text.toString().toInt(),money))
                importeApostadoNegro += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasNegro.text = importeApostadoNegro.toString() + "$"
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_background)
                binding.etCoinsToPlay.setText("0")

                }

            println(money)
        }

        binding.btnReset.setOnClickListener(){
            if(!apuestas.isNullOrEmpty()){
                val removerEstaApuesta = apuestas!!.get(apuestas!!.size-1)
                money += removerEstaApuesta.montoApostado

                if(removerEstaApuesta.seleccion == "Rojo"){
                    importeApostadoRojo -= removerEstaApuesta.montoApostado
                    binding.apuestasRojo.text = importeApostadoRojo.toString() + "$"
                }
                if(removerEstaApuesta.seleccion == "Negro"){
                    importeApostadoNegro -= removerEstaApuesta.montoApostado
                    binding.apuestasNegro.text = importeApostadoNegro.toString() + "$"
                }
                if(removerEstaApuesta.seleccion == "Verde"){
                    importeApostadoVerde -= removerEstaApuesta.montoApostado
                    binding.apuestasVerde.text = importeApostadoVerde.toString() + "$"
                }
                apuestas!!.removeLast()
            }
        }

        binding.btnPlay.setOnClickListener() {
            if (apuestas!!.isNotEmpty()) {

                binding.tvMoneyCount.text = money.toString() + "$"

                val randomNumber: Int = (0..10).random()

                binding.tvCardNumber.animate().apply {
                    duration = 400
                    rotationYBy(360f)
                }.start()

                binding.tvCardNumber.text = randomNumber.toString()

                if (randomNumber == 0) {
                    binding.tvCardNumber.setBackgroundResource(R.drawable.rounded_shape_green)
                    esVerde = true
                } else {
                    when (randomNumber % 2) {
                        0 -> {
                            binding.tvCardNumber.setBackgroundResource(R.drawable.rounded_shape_red)
                            esRojo = true
                        }
                        else -> {
                            binding.tvCardNumber.setBackgroundResource(R.drawable.rounded_shape_black)
                            esNegro = true
                        }
                    }
                }

            }
            for(apuesta in apuestas!!){
                //validar apuesta
                if(apuesta.seleccion == "Rojo" && esRojo == true ){
                    if(binding.tvCardNumber.text.toString().toInt() % 2 == 0){
                        println(apuesta.montoApostado * 2)
                        money += apuesta.montoApostado * 2
                        binding.tvMoneyCount.text = money.toString() + "$"
                    }
                }
                if(apuesta.seleccion == "Verde" && esVerde == true ){
                    if(binding.tvCardNumber.text.toString().toInt() == 0){
                        money += apuesta.montoApostado * 10
                        binding.tvMoneyCount.text = money.toString() + "$"
                    }
                }
                if(apuesta.seleccion == "Negro" && esNegro == true ){
                    if(binding.tvCardNumber.text.toString().toInt() % 2 != 0){
                        println(apuesta.montoApostado * 2)
                        money += apuesta.montoApostado * 2
                        binding.tvMoneyCount.text = money.toString() + "$"
                    }
                }
                apuesta.dinero = money

                lifecycleScope.launch(Dispatchers.IO){
                    app.apuestaDao().insert(apuesta)
                }

            }

            apuestas!!.clear()
            binding.apuestasVerde.text = "0$"
            binding.apuestasNegro.text = "0$"
            binding.apuestasRojo.text = "0$"
            importeApostadoNegro = 0
            importeApostadoRojo = 0
            importeApostadoVerde = 0
            esRojo = false
            esNegro = false
            esVerde = false

            if(binding.tvMoneyCount.text.toString() == "0$"){
                binding.addMoreCoins.setVisibility(View.VISIBLE)
            }

        }

        binding.addMoreCoins.setOnClickListener(){
            lifecycleScope.launch(Dispatchers.IO){
                app.apuestaDao().insert(Apuesta(0,"Reset",0,100))
            }
            money = 100
            binding.tvMoneyCount.text = money.toString() + "$"
            binding.addMoreCoins.setVisibility(View.INVISIBLE)
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}