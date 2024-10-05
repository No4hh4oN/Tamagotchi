package com.example.tamagotchi

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class CatchMouse : AppCompatActivity() {
    // 쥐잡기 게임 변수
    private lateinit var timerText: TextView
    private lateinit var caughtText: TextView
    private lateinit var scoreText: TextView
    private lateinit var firstMouse: ImageView
    private lateinit var secondMouse: ImageView
    private lateinit var thirdMouse: ImageView
    private lateinit var catchMessage: TextView
    private lateinit var feverMessage: TextView
    private lateinit var exptext: TextView
    private var score = 0
    private var caughtMiceCount = 0
    private val random = Random()
    private lateinit var gameTimer: CountDownTimer
    private lateinit var mouseTimer: CountDownTimer
    private lateinit var tryAgainButton: Button
    private lateinit var goBackButton: Button
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.minigame_mouse_start) // 메인 레이아웃 설정

        startButton = findViewById(R.id.mouse_game_start)
        startButton.setOnClickListener {
            catch_cat_start() // 시작 버튼 클릭 시 게임 시작
        }
    }

    private fun catch_cat_start() { // 쥐 잡기 게임 구현
        setContentView(R.layout.minigame_mouse_play)

        timerText = findViewById(R.id.timer_text)
        caughtText = findViewById(R.id.editTextTextMultiLine2)
        scoreText = findViewById(R.id.m_score)
        firstMouse = findViewById(R.id.firstmouse)
        secondMouse = findViewById(R.id.secondmouse)
        thirdMouse = findViewById(R.id.thirdmouse)
        catchMessage = findViewById(R.id.CatchCatch)
        feverMessage = findViewById(R.id.FeverTime)
        tryAgainButton = findViewById(R.id.m_tryagain)
        goBackButton = findViewById(R.id.gobacktoselectmenu)
        exptext = findViewById(R.id.expText2)

        startGame()

        val mouseClickListener = View.OnClickListener { v ->
            incrementScore()
            catchMessage.visibility = View.VISIBLE
            v.visibility = View.INVISIBLE

            mouseTimer.cancel()
            showMouseAfterDelay()
        }

        firstMouse.setOnClickListener(mouseClickListener)
        secondMouse.setOnClickListener(mouseClickListener)
        thirdMouse.setOnClickListener(mouseClickListener)
    }

    private fun startGame() {
        score = 0
        caughtMiceCount = 0
        scoreText.text = score.toString()
        caughtText.text = "잡은 마리 수: $caughtMiceCount"
        firstMouse.visibility = View.INVISIBLE
        secondMouse.visibility = View.INVISIBLE
        thirdMouse.visibility = View.INVISIBLE
        catchMessage.visibility = View.INVISIBLE
        feverMessage.visibility = View.INVISIBLE
        tryAgainButton.visibility = View.GONE
        goBackButton.visibility = View.GONE

        gameTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                timerText.text = String.format("%02d:%02d", minutes, seconds)

                // 10초 남았을 때 피버타임 문구 표시
                if (minutes == 0L && seconds == 10L) {
                    showFeverTime()
                }
            }

            override fun onFinish() {
                timerText.text = "00:00"
                endGame()
            }
        }.start()
        showMouseAfterDelay()
    }

    private fun endGame() {
        firstMouse.visibility = View.INVISIBLE
        secondMouse.visibility = View.INVISIBLE
        thirdMouse.visibility = View.INVISIBLE
        catchMessage.visibility = View.INVISIBLE
        gameTimer.cancel()
        mouseTimer.cancel()
        tryAgainButton.visibility = View.VISIBLE // 다시하기 버튼 보이기
        goBackButton.visibility = View.VISIBLE // 게임 선택 화면으로 돌아가기 버튼 보이기
        exptext.visibility = View.VISIBLE

        if (score <= 1000){ // 경험치 10, 공 1
            MainScreen.character.gainExperience(10)
            exptext.setText("경험치를 10 획득했습니다!\n공 1개를 획득했습니다!")
            MainScreen.character.toys["ball"] = (MainScreen.character.toys["ball"] ?: 0) + 1
        }
        else if (score > 1000 && score <= 2000){ // 경험치 20, 공 1, 낚싯대 1
            MainScreen.character.gainExperience(20)
            MainScreen.character.toys["ball"] = (MainScreen.character.toys["ball"] ?: 0) + 1
            MainScreen.character.toys["fishingRod"] = (MainScreen.character.toys["fishingRod"] ?: 0) + 1
            exptext.setText("경험치를 20 획득했습니다!\n공 1개, 낚싯대 1개를 획득했습니다!")
        }
        else if (score > 2000){ // 경험치 30, 공 2, 낚싯대 1
            MainScreen.character.gainExperience(30)
            MainScreen.character.toys["ball"] = (MainScreen.character.toys["ball"] ?: 0) + 2
            MainScreen.character.toys["fishingRod"] = (MainScreen.character.toys["fishingRod"] ?: 0) + 1
            exptext.setText("경험치를 30 획득했습니다!\n공 2개, 낚싯대 1개를 획득했습니다!")
        }

        // 다시하기 버튼 클릭 이벤트 설정
        tryAgainButton.setOnClickListener {
            startGame() // 게임 다시 시작
        }

        // 게임 선택 화면으로 돌아가기 버튼 클릭 이벤트 설정
        goBackButton.setOnClickListener {
            finish() // 현재 액티비티 종료 (게임 선택 화면으로 돌아가기)
        }
    }

    private fun showMouseAfterDelay() {
        catchMessage.postDelayed({
            catchMessage.visibility = View.INVISIBLE
        }, 1000)

        mouseTimer = object : CountDownTimer(1000, 500) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                hideAllMice()
                val mouseToShow = random.nextInt(3)
                when (mouseToShow) {
                    0 -> firstMouse.visibility = View.VISIBLE
                    1 -> secondMouse.visibility = View.VISIBLE
                    2 -> thirdMouse.visibility = View.VISIBLE
                }
                mouseTimer.start()
            }
        }
        mouseTimer.start()
    }

    private fun hideAllMice() {
        firstMouse.visibility = View.INVISIBLE
        secondMouse.visibility = View.INVISIBLE
        thirdMouse.visibility = View.INVISIBLE
    }

    private fun incrementScore() {
        val currentTime = timerText.text.toString().split(":")
        val minutes = currentTime[0].toInt()
        val seconds = currentTime[1].toInt()
        val timeRemaining = minutes * 60 + seconds

        val scoreIncrement = if (timeRemaining <= 10) 200 else 100
        score += scoreIncrement
        scoreText.text = score.toString()
        caughtMiceCount++
        caughtText.text = "잡은 마리 수: $caughtMiceCount"
    }

    private fun showFeverTime() {
        feverMessage.visibility = View.VISIBLE
        feverMessage.postDelayed({
            feverMessage.visibility = View.INVISIBLE
        }, 10000)
    }
}
