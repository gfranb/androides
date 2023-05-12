package com.example.roulette

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.roulette.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val m0nNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId){

            R.id.jugar -> {

                var fragment: Fragment? = null
                fragment = GameFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameLayout,fragment).commit()
                return@OnNavigationItemSelectedListener true

            }

            R.id.clasificacion -> {

                var fragment: Fragment? = null
                fragment = ClasificacionFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameLayout,fragment).commit()

                return@OnNavigationItemSelectedListener true

            }

        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val db = Room.databaseBuilder(applicationContext,AppDatabase::class.java, "apuestas-roulete").build()
        var fragment: Fragment? = null
        fragment = GameFragment()

        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout,fragment).commit()
        }

        binding.bottomNavigationView.setOnItemSelectedListener(m0nNavMenu)

    }

}