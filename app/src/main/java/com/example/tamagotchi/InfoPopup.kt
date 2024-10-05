package com.example.tamagotchi

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InfoPopup(context:Context, y:Int, x:Int) : Dialog(context) {
    private lateinit var close: ImageView
    private lateinit var marker: ImageView
    private var yMap = y
    private var xMap = x

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.adventure_map)
        close = findViewById(R.id.closeMap)

        val groundArray = arrayOf(arrayOf(R.id.playermarker00, R.id.playermarker01, R.id.playermarker02, R.id.playermarker03),
            arrayOf(R.id.playermarker10, R.id.playermarker11, R.id.playermarker12, R.id.playermarker13),
            arrayOf(R.id.playermarker20, R.id.playermarker21, R.id.playermarker22, R.id.playermarker23),
            arrayOf(R.id.playermarker30, R.id.playermarker31, R.id.playermarker32, R.id.playermarkerxx),
            arrayOf(R.id.playermarker40, R.id.playermarker41, R.id.playermarker42, R.id.playermarker43),
            arrayOf(R.id.playermarker50, R.id.playermarker51, R.id.playermarker52, R.id.playermarkerxxx))

        marker = findViewById(groundArray[yMap][xMap])
        marker.visibility = View.VISIBLE

        close.setOnClickListener {
            dismiss()
        }


    }
}