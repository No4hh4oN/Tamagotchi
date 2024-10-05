package com.example.tamagotchi

import java.io.Serializable
import kotlin.math.exp

class PlayerStatus() {
    var name: String? = ""
    var hunger: Int = 100
    var fullness: Int = 100
    var level: Int = 1
    var experience: Int = 0
    var emotion: Int = 50
    var health: Int = 50
    var feed: MutableMap<String, Int> = mutableMapOf("can" to 20, "catFeed" to 20) // o
    var medicine: MutableMap<String, Int> = mutableMapOf("pill" to 10, "injection" to 10) // o
    var toys: MutableMap<String, Int> = mutableMapOf("ball" to 10, "fishingRod" to 10) // o
    var color: String? = "default" // o

    constructor(name:String?, color:String?):this(){
        this.name = name
        this.color = color
    }

    constructor(name:String, hunger:Int, fullness:Int, level:Int, experience:Int, emotion:Int, health:Int, feed:MutableMap<String, Int>, medicine:MutableMap<String, Int>, toys:MutableMap<String, Int>, color:String):this() {
        this.name = name
        this.hunger = hunger
        this.fullness = fullness
        this.level = level
        this.experience = experience
        this.emotion = emotion
        this.health = health
        this.feed = feed
        this.medicine = medicine
        this.toys = toys
        this.color = color
    }

    fun feed(feedType: String) {
        when (feedType) {
            "can" -> {
                hunger = (hunger + 10).coerceAtMost(100)
                fullness = (fullness + 10).coerceAtMost(100)
                gainExperience(10)
            }
            "catFeed" -> {
                hunger = (hunger + 20).coerceAtMost(100)
                fullness = (fullness + 20).coerceAtMost(100)
                gainExperience(20)
            }
        }
        val currentFeedCount = feed[feedType] ?: 0
        if (currentFeedCount > 0) {
            feed[feedType] = currentFeedCount - 1
        }
    }

    fun useMedicine(medicineType: String) {
        when (medicineType) {
            "pill" -> {
                if (health < 50) {
                    health = (health + 5).coerceAtMost(50)
                }
            }
            "injection" -> {
                if (health < 50) {
                    health = (health + 10).coerceAtMost(50)
                }
            }
        }
        val currentMedicineCount = medicine[medicineType] ?: 0
        if (currentMedicineCount > 0) {
            medicine[medicineType] = currentMedicineCount - 1
        }
    }

    fun useToy(toyType: String) {
        when (toyType) {
            "ball" -> {
                if (emotion < 50) {
                    emotion = (emotion + 5).coerceAtMost(50)
                }
            }
            "fishingRod" -> {
                if (emotion < 50) {
                    emotion = (emotion + 10).coerceAtMost(50)
                }
            }
        }
        val currentToyCount = toys[toyType] ?: 0
        if (currentToyCount > 0) {
            toys[toyType] = currentToyCount - 1
        }
    }

    companion object {
        const val maxLevel = 3
    }

    fun gainExperience(amount: Int) {
        if (level < maxLevel) {
            experience += amount
            if (experience >= 100) {
                level++
                experience -= 100
            }
        }
    }

    fun decreaseHunger() {
        hunger = (hunger - 1).coerceAtLeast(0)
    }

    fun decreaseFullness() {
        fullness = (fullness - 1).coerceAtLeast(0)
    }

    fun decreaseEmotion() {
        emotion = (emotion - 10).coerceAtLeast(0)
    }

    fun decreaseHealth() {
        health = (health - 10).coerceAtLeast(0)
    }

//    fun getEmotion(): String {
//        return when {
//            hunger < 30 -> "Hungry"
//            else -> ""
//        }
//    }
}