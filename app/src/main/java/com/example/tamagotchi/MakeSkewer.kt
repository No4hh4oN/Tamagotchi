package com.example.tamagotchi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MakeSkewer : AppCompatActivity() {

    private lateinit var startButton: Button

    private lateinit var meatButton: Button
    private lateinit var onionButton: Button
    private lateinit var tomatoButton: Button
    private lateinit var cornButton: Button

    private lateinit var ranColorViews: List<TextView>
    private lateinit var userColorViews: List<TextView>

    private lateinit var skewerCountTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var feverTextView: TextView
    private lateinit var exptext: TextView

    private lateinit var tryAgainButton: Button
    private lateinit var goBackButton: Button

    private val colors = listOf("#8F5B0C", "#FF4A4A", "#FFF963", "#40FF6A")
    private var userColors = mutableListOf<String>()
    private var skewerCount = 0
    private var score = 0
    private var isGameActive = true
    private var isFeverTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.minigame_skewer_start)

        startButton = findViewById(R.id.skewer_game_start)
        startButton.setOnClickListener {
            skewergame_start() // 시작 버튼 클릭 시 게임 시작
        }
    }

    private fun skewergame_start() {
        score = 0
        setContentView(R.layout.minigame_skewer_play)

        meatButton = findViewById(R.id.meatButton)
        onionButton = findViewById(R.id.onionButton)
        tomatoButton = findViewById(R.id.tomatoButton)
        cornButton = findViewById(R.id.cornButton)
        exptext = findViewById(R.id.expText3)

        ranColorViews = listOf(
            findViewById(R.id.ranColor1),
            findViewById(R.id.ranColor2),
            findViewById(R.id.ranColor3),
            findViewById(R.id.ranColor4)
        )

        userColorViews = listOf(
            findViewById(R.id.userColor1),
            findViewById(R.id.userColor2),
            findViewById(R.id.userColor3),
            findViewById(R.id.userColor4)
        )

        skewerCountTextView = findViewById(R.id.editTextNumber3)
        scoreTextView = findViewById(R.id.s_score)
        feverTextView = findViewById(R.id.feverTime2)

        tryAgainButton = findViewById(R.id.s_tryagain)
        goBackButton = findViewById(R.id.gobacktoselectmenu)

        feverTextView.visibility = View.GONE  // 게임 시작 시 피버 타임 텍스트 숨김

        startGameTimer()
        setRandomColors()

        meatButton.setOnClickListener { onColorButtonClicked("#8F5B0C") }
        onionButton.setOnClickListener { onColorButtonClicked("#40FF6A") }
        tomatoButton.setOnClickListener { onColorButtonClicked("#FF4A4A") }
        cornButton.setOnClickListener { onColorButtonClicked("#FFF963") }
    }

    private fun startGameTimer() {
        val timerTextView: TextView = findViewById(R.id.timer_text2)
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60)

                if (secondsRemaining == 10L) {
                    isFeverTime = true
                    feverTextView.visibility = View.VISIBLE
                    object : CountDownTimer(10000, 10000) {
                        override fun onTick(millisUntilFinished: Long) {
                            // No-op
                        }

                        override fun onFinish() {
                            feverTextView.visibility = View.GONE
                        }
                    }.start()
                }
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                isGameActive = false

                meatButton.isEnabled = false
                onionButton.isEnabled = false
                tomatoButton.isEnabled = false
                cornButton.isEnabled = false

                tryAgainButton.visibility = View.VISIBLE
                goBackButton.visibility = View.VISIBLE
                exptext.visibility = View.VISIBLE

                if (score <= 1000){ // 경험치 10, 통조림 1
                    MainScreen.character.gainExperience(10)
                    exptext.setText("경험치를 10 획득했습니다!\n통조림 1개를 획득했습니다!")
                    MainScreen.character.feed["can"] = (MainScreen.character.feed["can"] ?: 0) + 1
                }
                else if (score > 1000 && score <= 2000){ // 경험치 20, 통조림 1, 사료 1
                    MainScreen.character.gainExperience(20)
                    MainScreen.character.feed["can"] = (MainScreen.character.feed["can"] ?: 0) + 1
                    MainScreen.character.feed["catFeed"] = (MainScreen.character.feed["catFeed"] ?: 0) + 1
                    exptext.setText("경험치를 20 획득했습니다!\n통조림 1개, 사료 1개를 획득했습니다!")
                }
                else if (score > 2000){ // 경험치 30, 통조림 2, 사료 1
                    MainScreen.character.gainExperience(30)
                    MainScreen.character.feed["can"] = (MainScreen.character.feed["can"] ?: 0) + 2
                    MainScreen.character.feed["catFeed"] = (MainScreen.character.feed["catFeed"] ?: 0) + 1
                    exptext.setText("경험치를 30 획득했습니다!\n통조림 2개, 사료 1개를 획득했습니다!")
                }

                tryAgainButton.setOnClickListener {
                    skewergame_start() // 다시하기
                }

                goBackButton.setOnClickListener {
                    val intent = Intent(this@MakeSkewer, MinigameSelect::class.java)
                    startActivity(intent)
                    finish() // 현재 액티비티 종료
                }
            }
        }.start()
    }

    private fun setRandomColors() {
        ranColorViews.forEach {
            val randomColor = colors[Random.nextInt(colors.size)]
            it.setBackgroundColor(Color.parseColor(randomColor))
            it.tag = randomColor
        }
    }

    private fun onColorButtonClicked(color: String) {
        if (isGameActive && userColors.size < userColorViews.size) {
            userColors.add(color)
            updateUserColors()
            if (userColors.size == userColorViews.size) {
                checkColors()
            }
        }
    }

    private fun updateUserColors() {
        userColors.forEachIndexed { index, color ->
            userColorViews[index].setBackgroundColor(Color.parseColor(color))
            userColorViews[index].tag = color
        }
    }

    private fun checkColors() {
        val isMatch = ranColorViews.indices.all { index ->
            ranColorViews[index].tag == userColorViews[index].tag
        }
        if (isMatch) {
            skewerCount++
            skewerCountTextView.text = skewerCount.toString()

            val additionalScore = if (isFeverTime) 200 else 100
            score += additionalScore
            scoreTextView.text = score.toString()

            setRandomColors()
        }
        userColors.clear()
        resetUserColors()
    }

    private fun resetUserColors() {
        userColorViews.forEach {
            it.setBackgroundColor(Color.TRANSPARENT)
            it.tag = null
        }
    }
}
