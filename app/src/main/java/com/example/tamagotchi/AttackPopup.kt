package com.example.tamagotchi

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AttackPopup(context:Context, text: String, health: Int) : Dialog(context) {
    private lateinit var message: TextView
    private lateinit var totalHealth: TextView
    private lateinit var close: ImageView
    private var text = text
    private var health = health

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.startattack_popup)
        message = findViewById(R.id.message)
        totalHealth = findViewById(R.id.totalhealth)
        close = findViewById(R.id.closeAttack)

        message.setText(text)
        totalHealth.setText("현재 체력 : " + health)
        close.setOnClickListener {
            dismiss()
        }
    }
}