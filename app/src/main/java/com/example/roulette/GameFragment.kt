package com.example.roulette

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context.TELEPHONY_SERVICE
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
import android.media.MediaPlayer
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PackageManagerCompat


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

    var mMediaPlayer: MediaPlayer? = null
    private var playingMusic: Boolean = false
    private var soundPool: SoundPool? = null
    private val soundId = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(this.context, R.raw.ficha, 1)
        mMediaPlayer = MediaPlayer.create(this.context, R.raw.song)
        mMediaPlayer!!.stop()

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
                soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
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



        binding.btnSound.setOnLongClickListener() {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
            true
        }

        binding.btnSound.setOnClickListener(){
                if(this.playingMusic==false) {
                    if (mMediaPlayer == null) {
                        mMediaPlayer = MediaPlayer.create(this.context, R.raw.song)
                        mMediaPlayer!!.isLooping = true
                        mMediaPlayer!!.setVolume(1F,1F)
                        mMediaPlayer!!.start()
                    } else {
                        mMediaPlayer!!.isLooping = true
                        mMediaPlayer!!.setVolume(1F,1F)
                        mMediaPlayer!!.start()
                    }
                    binding.btnSound.setImageResource(android.R.drawable.ic_media_pause)
                    this.playingMusic=true;
                }else{
                    mMediaPlayer!!.pause();
                    binding.btnSound.setImageResource(android.R.drawable.ic_media_play)
                    this.playingMusic=false;
                }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data // The URI with the location of the file
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer.create(this.context, R.raw.song)
                mMediaPlayer!!.setDataSource(selectedFile.toString())
                mMediaPlayer!!.isLooping = true
                mMediaPlayer!!.setVolume(1F, 1F)
                mMediaPlayer!!.start()
            }else{
                if(playingMusic==true){
                    mMediaPlayer!!.stop()
                    mMediaPlayer!!.setDataSource(selectedFile.toString())
                    mMediaPlayer!!.start()
                }else{
                    mMediaPlayer!!.stop()
                    mMediaPlayer!!.setDataSource(selectedFile.toString())
                    mMediaPlayer!!.start()
                }
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

    override fun onResume(){
        super.onResume()
        if(playingMusic){
            mMediaPlayer!!.start()
        }
    }

    override fun onPause(){
        super.onPause()
        if(playingMusic){
            mMediaPlayer!!.pause()
        }
    }

    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (playingMusic) {
                    mMediaPlayer!!.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (playingMusic) {
                    mMediaPlayer!!.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (playingMusic) {
                    mMediaPlayer!!.setVolume(0.1F, 0.1F)
                }

            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (playingMusic) {
                    mMediaPlayer!!.setVolume(1F, 1F)
                    mMediaPlayer!!.start()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mgr = activity?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }



    val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //Incoming call: Pause music
                mMediaPlayer!!.pause()
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music
                if(mMediaPlayer != null){
                    mMediaPlayer!!.start()
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
                mMediaPlayer!!.pause()
            }
            super.onCallStateChanged(state, incomingNumber)
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                val columnIndex = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            } catch (e: java.lang.Exception) {

            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

}