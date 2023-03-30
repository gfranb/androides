package com.example.roulette

import android.graphics.Color
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    private fun changeNumber(binding: FragmentGameBinding) {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater,container,false)

        binding.tvMoneyCount.text = money.toString() + "$"

        binding.btnPlay.setOnClickListener() {

            if(binding.etCoinsToPlay.text.toString().toInt() <= money && binding.etCoinsToPlay.text.toString().toInt() != 0){

                money -=  binding.etCoinsToPlay.text.toString().toInt()
                binding.tvMoneyCount.text = money.toString() + "$"

                if(binding.btnRojo.isSelected()){
                    Apuesta("Rojo",binding.etCoinsToPlay.text.toString().toInt())
                }
                if(binding.btnVerde.isSelected()){
                    Apuesta("Verde",binding.etCoinsToPlay.text.toString().toInt())
                }
                if(binding.btnNegro.isSelected()){
                    Apuesta("Negro",binding.etCoinsToPlay.text.toString().toInt())
                }

                    val randomNumber: Int = (0..10).random()

                        binding.tvCardNumber.animate().apply {
                            duration = 400
                            rotationYBy(360f)
                        }.start()

                    binding.tvCardNumber.text = randomNumber.toString()

                    if(randomNumber == 0){
                        binding.tvCardNumber.setBackgroundResource(R.drawable.rounded_shape_green)
                    }else{
                        when(randomNumber%2){
                            0 -> binding.tvCardNumber.setBackgroundResource(R.drawable.rounded_shape_red)
                            else -> {
                                binding.tvCardNumber.setBackgroundResource(R.drawable.rounded_shape_black)
                            }
                        }
                    }

            }else{
                //Mensaje de error no se puede jugar
            }

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