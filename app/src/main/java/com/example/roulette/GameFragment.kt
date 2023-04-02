package com.example.roulette

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.roulette.databinding.FragmentGameBinding
import java.util.Random

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
    private var money: Int = 1000
    private var apuestas: ArrayList<Apuesta>? = null
    private var esRojo: Boolean? = null
    private var esVerde: Boolean? = null
    private var esNegro: Boolean? = null
    var importeApostadoNegro: Int = 0
    var importeApostadoRojo: Int = 0
    var importeApostadoVerde: Int = 0

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
        binding.tvMoneyCount.text = money.toString() + "$"

        if(apuestas == null){
            apuestas = ArrayList()
        }

        binding.btnRojo.setOnClickListener(){

           if(apuestas?.isEmpty() == true){
               apuestas = ArrayList()
           }
            if(money - binding.etCoinsToPlay.text.toString().toInt() >= 0){
                apuestas?.add(Apuesta("Rojo",binding.etCoinsToPlay.text.toString().toInt()))
                money -= binding.etCoinsToPlay.text.toString().toInt()
                importeApostadoRojo += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasRojo.text = importeApostadoRojo.toString() + "$"
            }else{

            }
            println(money)
        }

        binding.btnVerde.setOnClickListener(){

            if(apuestas?.isEmpty() == true){
                apuestas = ArrayList()
            }
            if(money - binding.etCoinsToPlay.text.toString().toInt() >= 0){
                apuestas?.add(Apuesta("Verde",binding.etCoinsToPlay.text.toString().toInt()))
                importeApostadoVerde += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasVerde.text = importeApostadoVerde.toString() + "$"
                money -= binding.etCoinsToPlay.text.toString().toInt()
            }else{

            }
            println(money)
        }

        binding.btnNegro.setOnClickListener(){

            if(apuestas?.isEmpty() == true){
                apuestas = ArrayList()
            }
            if(money - binding.etCoinsToPlay.text.toString().toInt() >= 0){
                apuestas?.add(Apuesta("Negro",binding.etCoinsToPlay.text.toString().toInt()))
                money -= binding.etCoinsToPlay.text.toString().toInt()
                importeApostadoNegro += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasNegro.text = importeApostadoNegro.toString() + "$"
            }else{

            }
            println(money)
        }

        binding.btnPlay.setOnClickListener() {
            println(apuestas)
            if(apuestas!!.isNotEmpty()){

                binding.tvMoneyCount.text = money.toString() + "$"

                val randomNumber: Int = (0..10).random()

                    binding.tvCardNumber.animate().apply {
                        duration = 400
                        rotationYBy(360f)
                    }.start()

                binding.tvCardNumber.text = randomNumber.toString()

                if(randomNumber == 0){
                    binding.tvCardNumber.setBackgroundResource(R.drawable.rounded_shape_green)
                    esVerde = true
                }else{
                    when(randomNumber%2){
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

            }else{
                //Mensajes de error no se puede jugar porque no se ha apostado

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

            }

            // Guardar el historico de la apuesta realizada en SQLLite
            // Importe del jugador

            apuestas!!.clear()
            binding.apuestasVerde.text = ""
            binding.apuestasNegro.text = ""
            binding.apuestasRojo.text = ""
            importeApostadoNegro = 0
            importeApostadoRojo = 0
            importeApostadoVerde = 0
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