package com.example.tamagotchi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StartScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.start_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.startScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var startlayout: View = findViewById(R.id.startScreen)

        startlayout.setOnClickListener {
            val intent: Intent = Intent(this, Login::class.java)
            startActivity(intent)

        }
    }
}