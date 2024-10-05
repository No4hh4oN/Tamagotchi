package com.example.tamagotchi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MinigameSelect: AppCompatActivity() {
    fun gameselect() { //게임 선택화면에 대한 함수
        val btn_start_adventure: Button = findViewById(R.id.adventure_menu)
        val btn_start_catchmouse: Button = findViewById(R.id.catchmouse_menu)
        val btn_start_makeskewer: Button = findViewById(R.id.makeskewer_menu)

        btn_start_adventure.setOnClickListener{
            var selectedgame1 = Intent(this, AdventureMain::class.java)
            startActivity(selectedgame1)
        }
        btn_start_catchmouse.setOnClickListener{
            val selectedgame2 = Intent(this, CatchMouse::class.java)
            startActivity(selectedgame2)
        }
        btn_start_makeskewer.setOnClickListener {
            val selectedgame3 = Intent(this, MakeSkewer::class.java)
            startActivity(selectedgame3)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.minigame_select)
        gameselect()
    }
}