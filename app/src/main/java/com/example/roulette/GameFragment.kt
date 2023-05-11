package com.example.roulette

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.WRITE_CALENDAR
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.CalendarContract
import android.provider.MediaStore
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.roulette.databinding.FragmentGameBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


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
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val PERMISSION_REQUEST_WRITE_CALENDAR = 1
    private val PERMISSION_REQUEST_READ_CALENDAR = 100

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

    // Contiene el objeto reproductor multimedia
    var mMediaPlayer: MediaPlayer? = null

    // Indica si está sonando la música
    private var playingMusic: Boolean = false

    // Indica si la musica es la por defecto
    private var defaultMusic = 1

    // Contiene la ruta de la musica seleccionada por el usuario
    private var musicPath: Uri? = null

    // Cotiene el objeto reproductor de animaciones sonoras
    private var soundPool: SoundPool? = null

    // Indica el ID del sonido a reproducir
    private val soundId = 1

    // Gestiona la selección de la musica de fondo por parte del usuario.
    val getContent = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val selectedFile = uri
            this.musicPath = selectedFile
            this.defaultMusic = 0
            if (mMediaPlayer !== null) {
                mMediaPlayer!!.stop()
                mMediaPlayer!!.release()
                mMediaPlayer = null
                binding.btnSound.setImageResource(android.R.drawable.ic_media_play)
                this.playingMusic = false;
            }
        } else {
            this.defaultMusic = 1
            this.musicPath = null
            if (mMediaPlayer !== null) {
                mMediaPlayer!!.stop()
                mMediaPlayer!!.release()
                mMediaPlayer = null
                binding.btnSound.setImageResource(android.R.drawable.ic_media_play)
                this.playingMusic = false;
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Inicializar el reproductor de animaciones sonoras
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(this.context, R.raw.ficha, 1)

    }

    private fun checkPermissions() {
        // Verifica si el usuario ha dado permiso para acceder a la ubicación
        if (ActivityCompat.checkSelfPermission(
                requireContext(), ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si el usuario aún no ha dado permisos, solicita el permiso
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), 1
            )
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkPermissions()
        val app = Room.databaseBuilder(
            requireActivity().applicationContext, ApuestaDB::class.java, "apuesta"
        ).fallbackToDestructiveMigration().build()

        lifecycleScope.launch(Dispatchers.IO) {
            if (app.apuestaDao().getAll().isEmpty()) {
                app.apuestaDao().insert(Apuesta(0, "Reset", 0, 100, null , null))
            }
            money = app.apuestaDao().obtenerDineroDisponible()
            println(money)

            withContext(Dispatchers.Main) {
                binding.tvMoneyCount.text = money.toString() + "$"
            }
        }

        if (apuestas == null) {
            apuestas = ArrayList()
        }

        binding.btnRojo.setOnClickListener() {

            if (apuestas?.isEmpty() == true) {
                apuestas = ArrayList()
            }

            if (binding.etCoinsToPlay.text.toString()
                    .toInt() <= 0 || binding.etCoinsToPlay.text.toString() == ""
            ) {

                binding.etCoinsToPlay.setError("Inserte una apuesta para jugar")
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_error_background)

            } else if (money - binding.etCoinsToPlay.text.toString().toInt() >= 0) {

                idApuesta++
                money -= binding.etCoinsToPlay.text.toString().toInt()
                apuestas?.add(
                    Apuesta(
                        0, "Rojo", binding.etCoinsToPlay.text.toString().toInt(), money, null ,null
                    )
                )
                importeApostadoRojo += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasRojo.text = importeApostadoRojo.toString() + "$"
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_background)
                binding.etCoinsToPlay.setText("0")

            }

            println(money)

        }

        binding.btnVerde.setOnClickListener() {

            if (apuestas?.isEmpty() == true) {
                apuestas = ArrayList()
            }

            if (binding.etCoinsToPlay.text.toString()
                    .toInt() <= 0 || binding.etCoinsToPlay.text.toString() == ""
            ) {

                binding.etCoinsToPlay.setError("Inserte una apuesta para jugar")
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_error_background)

            } else if (money - binding.etCoinsToPlay.text.toString().toInt() >= 0) {

                idApuesta++
                money -= binding.etCoinsToPlay.text.toString().toInt()
                apuestas?.add(
                    Apuesta(
                        0, "Verde", binding.etCoinsToPlay.text.toString().toInt(), money, null, null
                    )
                )
                importeApostadoVerde += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasVerde.text = importeApostadoVerde.toString() + "$"
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_background)
                binding.etCoinsToPlay.setText("0")
            }
            println(money)
        }

        binding.btnNegro.setOnClickListener() {

            if (apuestas?.isEmpty() == true) {
                apuestas = ArrayList()
            }

            if (binding.etCoinsToPlay.text.toString()
                    .toInt() <= 0 || binding.etCoinsToPlay.text.toString() == ""
            ) {

                binding.etCoinsToPlay.setError("Inserte una apuesta para jugar")
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_error_background)

            } else if (money - binding.etCoinsToPlay.text.toString().toInt() >= 0) {

                idApuesta++
                money -= binding.etCoinsToPlay.text.toString().toInt()
                apuestas?.add(
                    Apuesta(
                        0, "Negro", binding.etCoinsToPlay.text.toString().toInt(), money
                    ,null ,null)
                )
                importeApostadoNegro += binding.etCoinsToPlay.text.toString().toInt()
                binding.apuestasNegro.text = importeApostadoNegro.toString() + "$"
                binding.etCoinsToPlay.setBackgroundResource(R.drawable.edit_text_background)
                binding.etCoinsToPlay.setText("0")

            }

            println(money)
        }

        binding.btnReset.setOnClickListener() {
            if (!apuestas.isNullOrEmpty()) {
                val removerEstaApuesta = apuestas!!.get(apuestas!!.size - 1)
                money += removerEstaApuesta.montoApostado

                if (removerEstaApuesta.seleccion == "Rojo") {
                    importeApostadoRojo -= removerEstaApuesta.montoApostado
                    binding.apuestasRojo.text = importeApostadoRojo.toString() + "$"
                }
                if (removerEstaApuesta.seleccion == "Negro") {
                    importeApostadoNegro -= removerEstaApuesta.montoApostado
                    binding.apuestasNegro.text = importeApostadoNegro.toString() + "$"
                }
                if (removerEstaApuesta.seleccion == "Verde") {
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

            var apuestaGanada = false

            for (apuesta in apuestas!!) {
                //validar apuesta
                if (apuesta.seleccion == "Rojo" && esRojo == true) {
                    if (binding.tvCardNumber.text.toString().toInt() % 2 == 0) {
                        println(apuesta.montoApostado * 2)
                        money += apuesta.montoApostado * 2
                        binding.tvMoneyCount.text = money.toString() + "$"
                    }
                    apuestaGanada = true
                    Toast.makeText(context, getString(R.string.win_red), Toast.LENGTH_LONG).show()
                }
                if (apuesta.seleccion == "Verde" && esVerde == true) {
                    if (binding.tvCardNumber.text.toString().toInt() == 0) {
                        money += apuesta.montoApostado * 10
                        binding.tvMoneyCount.text = money.toString() + "$"
                    }
                    apuestaGanada = true
                    Toast.makeText(context, getString(R.string.win_green), Toast.LENGTH_LONG).show()
                }
                if (apuesta.seleccion == "Negro" && esNegro == true) {
                    if (binding.tvCardNumber.text.toString().toInt() % 2 != 0) {
                        println(apuesta.montoApostado * 2)
                        money += apuesta.montoApostado * 2
                        binding.tvMoneyCount.text = money.toString() + "$"
                    }
                    apuestaGanada = true
                    Toast.makeText(context, getString(R.string.win_black), Toast.LENGTH_LONG).show()
                }

                apuesta.dinero = money


                if (apuestaGanada) {
                    insertLatLonUser(apuesta, true) { apuestaConLatLon ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            app.apuestaDao().insert(apuestaConLatLon)
                        }
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        app.apuestaDao().insert(apuesta)
                    }
                }

            }

            if (apuestaGanada) {
                addEventToCalendar()
                saveScreenshot(takeScreenshot(requireView()), requireContext())
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

            if (binding.tvMoneyCount.text.toString() == "0$") {
                binding.addMoreCoins.setVisibility(View.VISIBLE)
            }

        }

        binding.addMoreCoins.setOnClickListener() {
            lifecycleScope.launch(Dispatchers.IO) {
                app.apuestaDao().insert(Apuesta(0, "Reset", 0, 100, null, null))
            }
            money = 100
            binding.tvMoneyCount.text = money.toString() + "$"
            binding.addMoreCoins.setVisibility(View.INVISIBLE)
        }


        // Funcionamiento del boton de musica de fondo en pulsacion larga.
        binding.btnSound.setOnLongClickListener {
            val mimeTypes = arrayOf("audio/*")
            getContent.launch(mimeTypes)
            true
        }

        // Funcionamiento del boton de musica en pulsacion corta.
        binding.btnSound.setOnClickListener() {
            if (this.playingMusic == false) {
                if (mMediaPlayer == null && this.defaultMusic == 1) {
                    mMediaPlayer = MediaPlayer.create(this.context, R.raw.song)
                    mMediaPlayer!!.isLooping = true
                    mMediaPlayer!!.setVolume(1F, 1F)
                    mMediaPlayer!!.start()
                } else if (mMediaPlayer == null && this.defaultMusic == 0) {
                    mMediaPlayer = MediaPlayer.create(this.context, this.musicPath)
                    mMediaPlayer!!.isLooping = true
                    mMediaPlayer!!.setVolume(1F, 1F)
                    mMediaPlayer!!.start()
                } else {
                    mMediaPlayer!!.isLooping = true
                    mMediaPlayer!!.setVolume(1F, 1F)
                    mMediaPlayer!!.start()
                }
                binding.btnSound.setImageResource(android.R.drawable.ic_media_pause)
                this.playingMusic = true;
            } else {
                mMediaPlayer!!.pause();
                binding.btnSound.setImageResource(android.R.drawable.ic_media_play)
                this.playingMusic = false;
            }
        }

        return binding.root
    }

    private fun insertLatLonUser(
        apuesta: Apuesta, apuestaGanada: Boolean, callback: (Apuesta) -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermissions()
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val latitud = if (apuestaGanada) location?.latitude else null
            val longitud = if (apuestaGanada) location?.longitude else null
            Log.e("latitud",location?.latitude.toString())
            Log.e("longitud",location?.longitude.toString())
            Log.e("gane la apuesta?", apuestaGanada.toString())
            val apuestaConLatLon = apuesta.copy(
                latitud = latitud,
                longitud = longitud
            )
            callback(apuestaConLatLon)
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
        fun newInstance(param1: String, param2: String) = GameFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    // Que hacer con la musica cuando la aplicación vuelve al foco.
    override fun onResume() {
        super.onResume()
        if (playingMusic) {
            mMediaPlayer!!.start()
        }
    }

    // Que hacer con la musica cuando la aplicación deja el foco.
    override fun onPause() {
        super.onPause()
        if (playingMusic) {
            mMediaPlayer!!.pause()
        }
    }

    // Gestion de la musica cuando otra aplicación quiere el foco de audio.
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

        // Activar la gestion de la musica cuando entran llamadas.
        //Esto peta y da error.
        //val mgr = activity?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)

    }

    // Gestion de la musica cuando entran llamadas.
    val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //Incoming call: Pause music
                mMediaPlayer!!.pause()
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music
                if (mMediaPlayer != null) {
                    mMediaPlayer!!.start()
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
                mMediaPlayer!!.pause()
            }
            super.onCallStateChanged(state, incomingNumber)
        }
    }

    fun takeScreenshot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun saveScreenshot(bitmap: Bitmap, context: Context) {
        val relativeLocation = Environment.DIRECTORY_PICTURES + "/Capturas de pantalla"
        val filename =
            "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //this one
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
            }
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val outputStream = resolver.openOutputStream(imageUri!!)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.close()
        MediaScannerConnection.scanFile(
            context, arrayOf(imageUri.path), arrayOf("image/jpeg"), null
        )
    }

    fun addEventToCalendar() {
        // Verificar permiso de escritura en calendario
        if (ContextCompat.checkSelfPermission(
                requireContext(), WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(WRITE_CALENDAR), PERMISSION_REQUEST_WRITE_CALENDAR
            )
            return
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(), READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(READ_CALENDAR), PERMISSION_REQUEST_READ_CALENDAR
            )
            return
        }

        // Obtener el ID del calendario predeterminado de Google
        val projection = arrayOf(
            CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_TYPE
        )
        val selection =
            "${CalendarContract.Calendars.IS_PRIMARY} = 1 AND ${CalendarContract.Calendars.ACCOUNT_TYPE} = ?"
        val selectionArgs = arrayOf("com.google")
        val cursor = requireContext().contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI, projection, selection, selectionArgs, null
        )

        var calendarId = -1
        if (cursor != null && cursor.moveToFirst()) {
            calendarId = cursor.getInt(0)
            cursor.close()
        }

        // Guardar evento en el calendario predeterminado
        val beginTime: Long = System.currentTimeMillis()
        val endTime: Long = beginTime + (60 * 60 * 1000)
        val timeZone: TimeZone = TimeZone.getDefault()

        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE, getString(R.string.victory_calendar))
            put(
                CalendarContract.Events.DESCRIPTION,
                getString(R.string.victory_calendar_description)
            )
            put(CalendarContract.Events.DTSTART, beginTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.id)
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.HAS_ALARM, 1)
        }

        val uri =
            requireContext().contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

        if (uri != null) {
            // Verificar que el evento se ha guardado en el calendario predeterminado
            val eventProjection = arrayOf(CalendarContract.Events.CALENDAR_ID)
            val eventCursor = requireContext().contentResolver.query(
                uri, eventProjection, null, null, null
            )

            if (eventCursor != null && eventCursor.moveToFirst()) {
                val savedCalendarId = eventCursor.getInt(0)
                if (savedCalendarId == calendarId) {
                    println("Evento guardado en el calendario predeterminado")
                    println(uri.toString())
                } else {
                    println("Error: el evento no se ha guardado en el calendario predeterminado")
                }
                eventCursor.close()
            }
        } else {
            println("Error guardando el evento en el calendario")
        }
    }

}