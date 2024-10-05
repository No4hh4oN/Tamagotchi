package com.example.tamagotchi

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LookCharacter : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.look_character)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MedicineImage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val userId = getUserId()
        loadCharacterStatus(userId)
    }

    private fun getUserId(): String {
        val currentUser = auth.currentUser
        return currentUser?.uid ?: "unknown_user"
    }

    private fun loadCharacterStatus(userId: String) {
        val userStatusRef = database.child("tamagotchi").child("users").child(userId)
        userStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userStatus = dataSnapshot.getValue(PlayerStatus::class.java)
                    userStatus?.let {
                        updateUI(it)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
            }
        })
    }

    private fun updateUI(userStatus: PlayerStatus) {
        val name = userStatus.name
        val level = userStatus.level.toString()
        val fullness = userStatus.hunger
        val emotion = userStatus.emotion
        val health = userStatus.health
        val color = userStatus.color

        findViewById<TextView>(R.id.name).text = name
        findViewById<TextView>(R.id.levelNum2).text = level

        val hungerImage = findViewById<ImageView>(R.id.hungerStatus)
        val emotionImage = findViewById<ImageView>(R.id.emotionStatus)
        val healthImage = findViewById<ImageView>(R.id.healthStatus)

        // 포만감 이미지 업데이트
        when {
            fullness == 100 -> hungerImage.setImageResource(R.drawable.hungry5)
            fullness >= 80 -> hungerImage.setImageResource(R.drawable.hungry4)
            fullness >= 60 -> hungerImage.setImageResource(R.drawable.hungry3)
            fullness >= 40 -> hungerImage.setImageResource(R.drawable.hungry2)
            fullness >= 20 -> hungerImage.setImageResource(R.drawable.hungry1)
            else -> hungerImage.setImageResource(R.drawable.hungry0)
        }

        // 감정 이미지 업데이트
        when {
            emotion == 50 -> emotionImage.setImageResource(R.drawable.emotion5)
            emotion >= 40 -> emotionImage.setImageResource(R.drawable.emotion4)
            emotion >= 30 -> emotionImage.setImageResource(R.drawable.emotion3)
            emotion >= 20 -> emotionImage.setImageResource(R.drawable.emotion2)
            emotion >= 10 -> emotionImage.setImageResource(R.drawable.emotion1)
            else -> emotionImage.setImageResource(R.drawable.emotion0)
        }

        // 건강 이미지 업데이트
        when {
            health == 50 -> healthImage.setImageResource(R.drawable.health5)
            health >= 40 -> healthImage.setImageResource(R.drawable.health4)
            health >= 30 -> healthImage.setImageResource(R.drawable.health3)
            health >= 20 -> healthImage.setImageResource(R.drawable.health2)
            health >= 10 -> healthImage.setImageResource(R.drawable.health1)
            else -> healthImage.setImageResource(R.drawable.health0)
        }

        // 캐릭터 이미지 업데이트
        val characterImageView = findViewById<ImageView>(R.id.characterImageView)
        val imageResId = when {
            color == "black" && fullness <= 40 && level.toInt() == 1 -> R.drawable.character_level1_sick
            color == "black" && level.toInt() == 1 -> R.drawable.character_level_1
            color == "black" && fullness <= 40 && level.toInt() == 2 -> R.drawable.black_baby_sick
            color == "black" && level.toInt() ==2-> R.drawable.black_baby
            color == "black" && fullness <= 40 && level.toInt() == 3 -> R.drawable.black_sad
            color == "black" && emotion == 50 && health ==50 &&level.toInt() == 3 -> R.drawable.black_happy
            color == "black" && emotion <= 20 && level.toInt() == 3 -> R.drawable.black_boring
            color == "black" && health <= 10 && level.toInt() == 3 -> R.drawable.black_sick
            color == "black" && health == 0 && level.toInt() == 3 -> R.drawable.black_dead
            color == "black" && level.toInt() == 3 -> R.drawable.black_normal

            color == "brown" && fullness <= 40 && level.toInt() == 1 -> R.drawable.character_level1_sick
            color == "brown" && level.toInt() == 1 -> R.drawable.character_level_1
            color == "brown" && fullness <= 40 && level.toInt() == 2 -> R.drawable.brown_baby_sick
            color == "brown" && level.toInt() ==2-> R.drawable.brown_baby
            color == "brown" && fullness <= 40 && level.toInt() == 3 -> R.drawable.brown_sad
            color == "brown" && emotion == 50 && health ==50 &&level.toInt() == 3 -> R.drawable.brown_happy
            color == "brown" && emotion <= 20 && level.toInt() == 3 -> R.drawable.brown_boring
            color == "brown" && health <= 10 && level.toInt() == 3 -> R.drawable.brown_sick
            color == "brown" && health == 0 && level.toInt() == 3 -> R.drawable.brown_dead
            color == "brown" && level.toInt() == 3 -> R.drawable.brown_normal

            color == "white" && fullness <= 40 && level.toInt() == 1 -> R.drawable.character_level1_sick
            color == "white" && level.toInt() == 1 -> R.drawable.character_level_1
            color == "white" && fullness <= 40 && level.toInt() == 2 -> R.drawable.white_baby_sick
            color == "white" && level.toInt() ==2-> R.drawable.white_baby
            color == "white" && fullness <= 40 && level.toInt() == 3 -> R.drawable.white_sad
            color == "white" && emotion == 50 && health ==50 &&level.toInt() == 3 -> R.drawable.white_happy
            color == "white" && emotion <= 20 && level.toInt() == 3 -> R.drawable.white_boring
            color == "white" && health <= 10 && level.toInt() == 3 -> R.drawable.white_sick
            color == "white" && health == 0 && level.toInt() == 3 -> R.drawable.white_dead
            color == "white" && level.toInt() == 3 -> R.drawable.white_normal
            else -> R.drawable.default0 // 기본 이미지

        }
        characterImageView.setImageResource(imageResId)
    }
}