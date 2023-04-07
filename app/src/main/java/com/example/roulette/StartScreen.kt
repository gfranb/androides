package com.example.roulette

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.roulette.databinding.ActivityStartScreenBinding

class StartScreen : AppCompatActivity() {

    private lateinit var binding: ActivityStartScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hago el binding y lo muestro e inflo la vista.

        binding = ActivityStartScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener(){
            val context = binding.btnStart.context
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

    }
}