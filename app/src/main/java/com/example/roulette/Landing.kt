package com.example.roulette

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.roulette.databinding.ActivityLandingBinding


class Landing : AppCompatActivity() {

    lateinit var binding: ActivityLandingBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener(){
            val context = binding.btnNext.context
            val intent = Intent(context, StartScreen::class.java)
            context.startActivity(intent)
        }

        // Boton de ayuda
        binding.btnHelp.setOnClickListener(){
            val context = binding.btnHelp.context
            val intent = Intent(context, Help::class.java)
            startActivity(intent)
        }
    }
}