package com.example.tamagotchi

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingPopupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.setting_popup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MedicineImage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var mediaPlayer = MediaPlayer.create(this, null) // 현재 들어간 노래가 없어서 url에 null값이 들어가 있다. (노래는 이번 개발에서는 추가할 생각이 없다)

        val soundtoggle: ImageView = findViewById(R.id.onoffImage)
        var isSoundOn: Boolean = true

        soundtoggle.setOnClickListener {
            if (isSoundOn) {
                // 소리가 켜져 있는 상태에서 버튼을 클릭하면 소리를 끕니다.
                mediaPlayer.pause()
                isSoundOn = false
                soundtoggle.setImageResource(R.drawable.on) // 이미지 변경
            } else {
                // 소리가 꺼져 있는 상태에서 버튼을 클릭하면 소리를 켭니다.
                mediaPlayer.start()
                isSoundOn = true
                soundtoggle.setImageResource(R.drawable.off) // 이미지 변경
            }
        }

    }
}