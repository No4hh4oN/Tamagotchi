package com.example.tamagotchi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CharacterSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.character_select)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MedicineImage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val startButton: Button = findViewById(R.id.startButton)
        val nameText: EditText = findViewById(R.id.naming)

        val character = findViewById<ImageView>(R.id.selectCharacterImage)
        val previousButton: Button = findViewById(R.id.previousbutton) // 왼쪽 버튼 변수
        val nextButton: Button = findViewById(R.id.nextbutton) // 오른쪽 버튼 변수
        var index = 2
        var selectedColor = "black" // 초기 캐릭터 색상

        // 오른쪽 버튼 누르기
        nextButton.setOnClickListener {
            when (index) {
                2 -> {
                    character.setImageResource(R.drawable.brown_normal)
                    index = 3
                    selectedColor = "brown"
                }
                3 -> {
                    character.setImageResource(R.drawable.white_normal)
                    index = 1
                    selectedColor = "white"
                }
                else -> {
                    character.setImageResource(R.drawable.black_normal)
                    index = 2
                    selectedColor = "black"
                }
            }
        }

        // 왼쪽 버튼 누르기
        previousButton.setOnClickListener {
            when (index) {
                1 -> {
                    character.setImageResource(R.drawable.brown_normal)
                    index = 3
                    selectedColor = "brown"
                }
                2 -> {
                    character.setImageResource(R.drawable.white_normal)
                    index = 1
                    selectedColor = "white"
                }
                else -> {
                    character.setImageResource(R.drawable.black_normal)
                    index = 2
                    selectedColor = "black"
                }
            }
        }

        startButton.setOnClickListener {
            val intent = Intent(this, MainScreen::class.java).apply {
                putExtra("name", nameText.text.toString())
                putExtra("color", selectedColor)
            }
            startActivity(intent)
        }
    }
}

