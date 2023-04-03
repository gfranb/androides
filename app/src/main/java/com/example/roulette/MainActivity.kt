package com.example.roulette

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.roulette.databinding.ActivityMainBinding
import com.example.roulette.databinding.FragmentGameBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val m0nNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId){
            R.id.jugar -> {
                supportFragmentManager.commit{
                    replace<GameFragment>(R.id.frameLayout)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.clasificacion -> {
                supportFragmentManager.commit{
                    replace<ClasificacionFragment>(R.id.frameLayout)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.commit{
            replace<GameFragment>(R.id.frameLayout)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }

        binding.bottomNavigationView.setOnItemSelectedListener(m0nNavMenu)

    }

}