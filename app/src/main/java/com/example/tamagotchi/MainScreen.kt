package com.example.tamagotchi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import java.io.Serializable

class MainScreen : AppCompatActivity() {
    companion object{
        var character: PlayerStatus = PlayerStatus()
    }
    private lateinit var characterImageView: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private val hungerUpdateInterval = 1000L // 1초당 -1
    private val fullnessUpdateInterval = 1000L // 1초당 -1
    private val emotionUpdateInterval = 15000L // 15초당 -10
    private val healthUpdateInterval = 15000L // 15초당 -10
    private val saveInterval = 1000L // 1초마다 데이터베이스에 저장
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var nametext: TextView
    private lateinit var experienceProgressBar: ProgressBar

    private val saveToDatabaseRunnable = object : Runnable {
        override fun run() {
            Log.d("+++++", character.name.isNullOrEmpty().toString())
            if (!character.name.isNullOrEmpty()) {
                Log.d("+++++", "saveToDatabaseRunnable, 45줄")
                saveUserStatus(getUserId(), character)
                handler.postDelayed(this, saveInterval)
            }
        }
    }

    private val hungerUpdater = object : Runnable {
        override fun run() {
            character.decreaseHunger()
            updateCharacterImage()
            handler.postDelayed(this, hungerUpdateInterval)
        }
    }

    private val fullnessUpdater = object : Runnable {
        override fun run() {
            character.decreaseFullness()
            updateCharacterImage()
            handler.postDelayed(this, fullnessUpdateInterval)
        }
    }

    private val emotionUpdater = object : Runnable {
        override fun run() {
            character.decreaseEmotion()
            updateCharacterImage()
            handler.postDelayed(this, emotionUpdateInterval)
        }
    }

    private val healthUpdater = object : Runnable {
        override fun run() {
            character.decreaseHealth()
            updateCharacterImage()
            handler.postDelayed(this, healthUpdateInterval)
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MedicineImage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Firebase 데이터베이스 참조 초기화
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // 설정한 이름 가져오기
        val nameFromIntent = intent.getStringExtra("name")
        val colorFromIntent = intent.getStringExtra("color")
        nametext = findViewById(R.id.name)

        character = PlayerStatus(nameFromIntent, colorFromIntent)
        character.color = colorFromIntent ?: "default"
        characterImageView = findViewById(R.id.characterImageView)
        experienceProgressBar = findViewById(R.id.experience) // 경험치 ProgressBar 초기화

        val userId = getUserId()
        if (nameFromIntent != null && nameFromIntent.isNotEmpty()) {
            nametext.text = nameFromIntent
        }
        else {
            loadUserStatus(userId)
        }

        // 돋보기 버튼 누르기
        val statebutton: Button = findViewById(R.id.statebutton)
        statebutton.setOnClickListener {
            val look = Intent(this, LookCharacter::class.java)
            look.putExtra("name", nametext.text.toString())
            look.putExtra("level", character.level.toString())
            look.putExtra("fullness", character.fullness)
            look.putExtra("emotion", character.emotion)
            look.putExtra("health", character.health)
            startActivity(look)
        }

        //톱니바퀴 누르기
        val settingbutton: ImageView = findViewById(R.id.settingview)
        // 노래 추가하기
        var mediaPlayer = MediaPlayer.create(this, R.raw.mysong)
        mediaPlayer?.start()

        settingbutton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.setting_popup)
            dialog.window?.setLayout(370.dpToPx(), 250.dpToPx())
            val soundbutton = dialog.findViewById<ImageView>(R.id.onoffImage)

            // SharedPreferences 객체 생성
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            // 저장된 이미지 상태 불러오기

            var isImage1 = sharedPreferences.getBoolean("isImage1", true)
            if(isImage1)
                soundbutton.setImageResource(R.drawable.on)
            soundbutton.setOnClickListener {
                if (isImage1) {
                    soundbutton.setImageResource(R.drawable.on)
                    mediaPlayer.start()
                } else {
                    soundbutton.setImageResource(R.drawable.off)
                    mediaPlayer.pause()
                }
                isImage1 = !isImage1
                sharedPreferences.edit().putBoolean("isImage1", !isImage1).apply()
            }

            dialog.show()
        }

        // 먹이 주기 버튼
        findViewById<Button>(R.id.feedbutton).setOnClickListener {
            val canCount = character.feed["can"] ?: 0
            val catFeedCount = character.feed["catFeed"] ?: 0

            if (canCount > 0 || catFeedCount > 0) {
                val dialog2 = Dialog(this)
                dialog2.setContentView(R.layout.popup_feed)
                dialog2.window?.setLayout(350.dpToPx(), 350.dpToPx())
                val canImage = dialog2.findViewById<ImageView>(R.id.canImage)
                val catfeedImage = dialog2.findViewById<ImageView>(R.id.catfeedImage)
                val canQuantityText = dialog2.findViewById<TextView>(R.id.canquantity)
                val catFeedQuantityText = dialog2.findViewById<TextView>(R.id.fishingquantity)

                canQuantityText.text = character.feed["can"].toString()
                catFeedQuantityText.text = character.feed["catFeed"].toString()

                canImage.setOnClickListener {
                    if (character.hunger >= 100) {
                        Toast.makeText(this, "포만감이 최대입니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        character.feed("can")
                        val currentValue = canQuantityText.text.toString().toInt()
                        val newValue = currentValue - 1
                        if (newValue >= 0) {
                            canQuantityText.text = newValue.toString()
                        }
                        updateCharacterStatus()
                    }
                }

                catfeedImage.setOnClickListener {
                    if (character.hunger >= 100) {
                        Toast.makeText(this, "포만감이 최대입니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        character.feed("catFeed")
                        val currentValue = catFeedQuantityText.text.toString().toInt()
                        val newValue = currentValue - 1
                        if (newValue >= 0) {
                            catFeedQuantityText.text = newValue.toString()
                        }
                        updateCharacterStatus()
                    }
                }

                dialog2.setOnDismissListener {
                    Log.d("+++++", "feed button, 210줄")
                    saveUserStatus(userId, character)
                }

                dialog2.show()
            } else {
                Toast.makeText(this, "먹이가 부족합니다!", Toast.LENGTH_SHORT).show()
            }
        }

        // 약 주기 버튼
        findViewById<Button>(R.id.medicinebutton).setOnClickListener {
            val pillCount = character.medicine["pill"] ?: 0
            val injectionCount = character.medicine["injection"] ?: 0

            if (pillCount > 0 || injectionCount > 0) {
                val dialog3 = Dialog(this)
                dialog3.setContentView(R.layout.popup_medicine)
                dialog3.window?.setLayout(370.dpToPx(), 350.dpToPx())
                val pillImage = dialog3.findViewById<ImageView>(R.id.pillImage)
                val injectionImage = dialog3.findViewById<ImageView>(R.id.injectionImage)
                val pillQuantityText = dialog3.findViewById<TextView>(R.id.pillquantity)
                val injectionQuantityText = dialog3.findViewById<TextView>(R.id.injectionquantity)

                pillQuantityText.text = character.medicine["pill"].toString()
                injectionQuantityText.text = character.medicine["injection"].toString()

                pillImage.setOnClickListener {
                    if (character.health >= 50) {
                        Toast.makeText(applicationContext, "건강이 최대입니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        character.useMedicine("pill")
                        val currentValue = pillQuantityText.text.toString().toInt()
                        val newValue = currentValue - 1
                        if (newValue >= 0) {
                            pillQuantityText.text = newValue.toString()
                        }
                        updateCharacterStatus()
                    }
                }

                injectionImage.setOnClickListener {
                    if (character.health >= 50) {
                        Toast.makeText(applicationContext, "건강이 최대입니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        character.useMedicine("injection")
                        val currentValue = injectionQuantityText.text.toString().toInt()
                        val newValue = currentValue - 1
                        if (newValue >= 0) {
                            injectionQuantityText.text = newValue.toString()
                        }
                        updateCharacterStatus()
                    }
                }

                dialog3.setOnDismissListener {
                    Log.d("+++++", "medicine button, 266줄")
                    saveUserStatus(userId, character)
                }

                dialog3.show()
            } else {
                Toast.makeText(this, "약이 부족합니다!", Toast.LENGTH_SHORT).show()
            }
        }

        // 놀아주기 버튼
        findViewById<Button>(R.id.toybutton).setOnClickListener {
            val ballCount = character.toys["ball"] ?: 0
            val fishingRodCount = character.toys["fishingRod"] ?: 0

            if (ballCount > 0 || fishingRodCount > 0) {
                val dialog4 = Dialog(this)
                dialog4.setContentView(R.layout.popup_emotion)
                dialog4.window?.setLayout(370.dpToPx(), 350.dpToPx())
                val ballImage = dialog4.findViewById<ImageView>(R.id.ballImage)
                val fishingImage = dialog4.findViewById<ImageView>(R.id.fishingImage)
                val ballQuantityText = dialog4.findViewById<TextView>(R.id.ballquantity)
                val fishingQuantityText = dialog4.findViewById<TextView>(R.id.fishingquantity)

                ballQuantityText.text = character.toys["ball"].toString()
                fishingQuantityText.text = character.toys["fishingRod"].toString()

                ballImage.setOnClickListener {
                    if (character.emotion >= 50) {
                        Toast.makeText(applicationContext, "감정이 최대입니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        character.useToy("ball")
                        val currentValue = ballQuantityText.text.toString().toInt()
                        val newValue = currentValue - 1
                        if (newValue >= 0) {
                            ballQuantityText.text = newValue.toString()
                        }
                        updateCharacterStatus()
                    }
                }

                fishingImage.setOnClickListener {
                    if (character.emotion >= 50) {
                        Toast.makeText(applicationContext, "감정이 최대입니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        character.useToy("fishingRod")
                        val currentValue = fishingQuantityText.text.toString().toInt()
                        val newValue = currentValue - 1
                        if (newValue >= 0) {
                            fishingQuantityText.text = newValue.toString()
                        }
                        updateCharacterStatus()
                    }
                }

                dialog4.setOnDismissListener {
                    Log.d("+++++", "toy button, 322줄")
                    saveUserStatus(userId, character)
                }

                dialog4.show()
            } else {
                Toast.makeText(this, "장난감이 부족합니다!", Toast.LENGTH_SHORT).show()
            }
        }

        // 게임 선택화면 가기
        val gamebutton: Button = findViewById(R.id.gamebutton)
        gamebutton.setOnClickListener {
            val selectedgame = Intent(this, MinigameSelect::class.java)
            startActivity(selectedgame)
        }

        // 상태 업데이트 시작
        startStatusUpdater()
    }

    override fun onStart() {
        super.onStart()
        updateCharacterStatus()

    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun startStatusUpdater() {
            handler.postDelayed(hungerUpdater, hungerUpdateInterval)
            handler.postDelayed(fullnessUpdater, fullnessUpdateInterval)
            handler.postDelayed(emotionUpdater, emotionUpdateInterval)
            handler.postDelayed(healthUpdater, healthUpdateInterval)
            handler.postDelayed(saveToDatabaseRunnable, saveInterval) // 데이터베이스 저장 시작
    }

    private fun stopStatusUpdater() {
        Log.d("+++++", "")
        handler.removeCallbacks(hungerUpdater)
        handler.removeCallbacks(fullnessUpdater)
        handler.removeCallbacks(emotionUpdater)
        handler.removeCallbacks(healthUpdater)
        handler.removeCallbacks(saveToDatabaseRunnable) // 데이터베이스 저장 중지
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        stopStatusUpdater()
//
//    }

    private fun updateCharacterImage() {
        val level = character.level
        val imageResId = when {
            character.color == "black" && character.fullness <= 40 && level == 1 -> R.drawable.character_level1_sick
            character.color == "black" && level == 1 -> R.drawable.character_level_1
            character.color == "black" && character.fullness <= 40 && level == 2 -> R.drawable.black_baby_sick
            character.color == "black" && level == 2 -> R.drawable.black_baby
            character.color == "black" && character.fullness <= 40 && level == 3 -> R.drawable.black_sad
            character.color == "black" && character.emotion == 50 &&character.health ==50 &&level == 3 -> R.drawable.black_happy
            character.color == "black" && character.emotion <= 20 && level == 3 -> R.drawable.black_boring
            character.color == "black" && character.health <= 10 && level == 3 -> R.drawable.black_sick
            character.color == "black" && character.health == 0 && level == 3 -> R.drawable.black_dead
            character.color == "black" && level == 3 -> R.drawable.black_normal

            character.color == "brown" && character.fullness <= 40 && level == 1 -> R.drawable.character_level1_sick
            character.color == "brown" && level == 1 -> R.drawable.character_level_1
            character.color == "brown" && character.fullness <= 40 && level == 2 -> R.drawable.brown_baby_sick
            character.color == "brown" && level == 2 -> R.drawable.brown_baby
            character.color == "brown" && character.fullness <= 40 && level == 3 -> R.drawable.brown_sad
            character.color == "brown" && character.emotion == 50 &&character.health ==50 &&level == 3 -> R.drawable.brown_happy
            character.color == "brown" && character.emotion <= 20 && level == 3 -> R.drawable.brown_boring
            character.color == "brown" && character.health <= 10 && level == 3 -> R.drawable.brown_sick
            character.color == "brown" && character.health == 0 && level == 3 -> R.drawable.brown_dead
            character.color == "brown" && level == 3 -> R.drawable.brown_normal

            character.color == "white" && character.fullness <= 40 && level == 1 -> R.drawable.character_level1_sick
            character.color == "white" && level == 1 -> R.drawable.character_level_1
            character.color == "white" && character.fullness <= 40 && level == 2 -> R.drawable.white_baby_sick
            character.color == "white" && level == 2 -> R.drawable.white_baby
            character.color == "white" && character.fullness <= 40 && level == 3 -> R.drawable.white_sad
            character.color == "white" && character.emotion == 50 &&character.health ==50 &&level == 3 -> R.drawable.white_happy
            character.color == "white" && character.emotion <= 20 && level == 3 -> R.drawable.white_boring
            character.color == "white" && character.health <= 10 && level == 3 -> R.drawable.white_sick
            character.color == "white" && character.health == 0 && level == 3 -> R.drawable.white_dead
            character.color == "white" && level == 3 -> R.drawable.white_normal
            else -> R.drawable.default0 // 기본 이미지
        }
        characterImageView.setImageResource(imageResId)
    }

    private fun updateCharacterStatus() {
        val levelText: TextView = findViewById(R.id.levelNum)
        levelText.text = character.level.toString()

        experienceProgressBar.progress = character.experience
    }

    private fun saveUserStatus(userId: String, status: PlayerStatus) {
        database.child("tamagotchi").child("users").child(userId).setValue(status)
            .addOnSuccessListener {
                Log.d("MainScreen", "User status saved successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("MainScreen", "Failed to save user status", e)
            }
    }

    private fun loadUserStatus(userId: String) {
        val userStatusRef = database.child("tamagotchi").child("users").child(userId)
        userStatusRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {

                val userStatus = snapshot.getValue(PlayerStatus::class.java)
                userStatus?.let {
                    character.level = it.level
                    character.feed = it.feed.toMutableMap()
                    character.medicine = it.medicine.toMutableMap()
                    character.toys = it.toys.toMutableMap()
                    character.experience = it.experience
                    character.hunger = it.hunger
                    character.fullness = it.fullness
                    character.emotion = it.emotion
                    character.health = it.health
                    character.color = it.color
                    character.name = it.name
                    nametext.text = it.name
                    updateCharacterStatus()
                    Log.d("MainScreen", "User status loaded successfully.")
                }
            } else {
                val initialStatus = createInitialStatus()
                nametext.text = initialStatus.name
                Log.d("+++++", "loadUserStatus, 454줄")
                saveUserStatus(userId, character)
            }
            startStatusUpdater()
        }.addOnFailureListener { e ->
            Log.e("MainScreen", "Failed to load user status", e)
        }
    }

    private fun createInitialStatus(): PlayerStatus {
        return PlayerStatus(
            nametext.text.toString(),
            character.color
        )
    }

    private fun getUserId(): String {
        val currentUser = auth.currentUser
        return currentUser?.uid ?: "unknown_user"
    }


}
