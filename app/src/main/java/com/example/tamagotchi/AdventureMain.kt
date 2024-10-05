package com.example.tamagotchi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class AdventureMain : AppCompatActivity() {
    private var moveLevel = 0 // 이동력
    private var damageLevel = 0 // 공격력
    private var turnLevel = 0 // 현재 턴 수
    private var healLevel = 10 // 체력
    private var pointLevel = 0 // 점수
    private var x = 0 // 지형 x축
    private var y = 0 // 지형 y축
    private var hide = true // true = 회피, false = -1
    private var isTreasureChest = false // 보물 한번이라도 획득했는지
    private var TreasureChest = arrayOf(false, false, false, false)
    private var dragonCoin = 0 // 습격에서 뽑히면 체력 -1 (이동력 하나 소모할 때 마다 증가)
    private val moveLevelRandom = (1..10) // 이동력 랜덤
    private val damageLevelRandom = (1..10) // 공격력 랜덤
    private val turnRandom = (1..20) //턴 시작 시 습격 랜덤
    private val templeRandom = (1..20) //턴 시작 시 습격 랜덤
    private var turnRandomInfo = 0 // 턴 시작 시 습격이 되는지 안되는지
    private var templeRandomInfo = 0 // 사원 습격 or 버튼 선택 랜덤
    private var monsterMove : () -> Unit = {setMonsterViewVisible(true)}
    private val groundArray = arrayOf(arrayOf("시작점", "평지", "평지", "숲의 사원"),
        arrayOf("평지", "수풀", "수풀", "평지"),
        arrayOf("수풀", "숲의 사원", "보물상자A", "수풀"),
        arrayOf("평지", "수풀", "숲의 사원", "공허"),
        arrayOf("평지", "숲의 사원", "공허", "보물상자B"),
        arrayOf("보물상자C", "수풀", "보물상자D", "공허"))
    private var dragonMode = 0


    private lateinit var moveLeveltext: TextView// 이동력 텍스트뷰
    private lateinit var damageLeveltext: TextView// 공격력 텍스트뷰
    private lateinit var turnLeveltext: TextView// 현재 턴 텍스트뷰
    private lateinit var groundtext: TextView// 현재 지형 텍스트뷰
    private lateinit var pointLeveltext: TextView// 점수 텍스트뷰
    private lateinit var healLeveltext: TextView// 체력 텍스트뷰
    private lateinit var foresttext: TextView // 사원 메인 텍스트
    private lateinit var endingtext: TextView // 엔딩 텍스트
    private lateinit var exptext: TextView // 경험치, 아이템 텍스트
    private lateinit var upButton: ImageView // 업버튼
    private lateinit var downButton: ImageView // 다운버튼
    private lateinit var leftButton: ImageView// 왼쪽버튼
    private lateinit var rightButton: ImageView // 오른쪽버튼
    private lateinit var player: ImageView // 메인 캐릭터
    private lateinit var mapButton: ImageView // 메인 캐릭터
    private lateinit var forestButton1: Button // 사원 이동력 1 증가
    private lateinit var forestButton2: Button // 사원 공격력 1 증가
    private lateinit var forestButton3: Button // 사원 체력 1 증가
    private lateinit var goBackButton: Button // minigame으로 돌아감
    private lateinit var tryAgainButton: Button  // 다시 시작
    private lateinit var monstertext: TextView // 몬스터 메인 텍스트
    private lateinit var monsterButton1: Button // 몬스터 체력 1 감소
    private lateinit var monsterButton2: Button // 몬스터 공격력 1 감소

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adventure_main)
        moveLeveltext = findViewById(R.id.textMoveLevel)// 이동력 텍스트뷰
        damageLeveltext= findViewById(R.id.textDamageLevel)// 공격력 텍스트뷰
        turnLeveltext= findViewById(R.id.textTurnLevel)// 현재 턴 텍스트뷰
        groundtext = findViewById(R.id.textNowGrund)// 현재 지형 텍스트뷰
        pointLeveltext= findViewById(R.id.textPointLevel)// 점수 텍스트뷰
        healLeveltext= findViewById(R.id.textHealLevel)// 체력 텍스트뷰
        foresttext= findViewById(R.id.forestText) // 사원 메인 텍스트
        endingtext= findViewById(R.id.endingText) // 엔딩 텍스트
        exptext= findViewById(R.id.expText) // 엔딩 텍스트
        upButton= findViewById(R.id.buttonUp) // 업버튼
        downButton= findViewById(R.id.buttonDown) // 다운버튼
        leftButton= findViewById(R.id.buttonLeft) // 왼쪽버튼
        rightButton= findViewById(R.id.buttonRight) // 오른쪽버튼
        player= findViewById(R.id.mainChar) // 메인 캐릭터
        forestButton1= findViewById(R.id.forestButton1) // 사원 이동력 1 증가
        forestButton2= findViewById(R.id.forestButton2) // 사원 공격력 1 증가
        forestButton3= findViewById(R.id.forestButton3) // 사원 체력 1 증가
        goBackButton= findViewById(R.id.gobacktoselectmenu) // minigame으로 돌아감
        tryAgainButton= findViewById(R.id.m_tryagain) // 다시 시작
        monstertext = findViewById(R.id.monsterText) // 몬스터 메인 텍스트
        monsterButton1 = findViewById(R.id.monsterButton1) // 몬스터 체력 1 감소
        monsterButton2 = findViewById(R.id.monsterButton2) // 몬스터 공격력 1 감소
        mapButton = findViewById(R.id.mapButton) // 맵 버튼

        mapButton.setOnClickListener {
            val popup = InfoPopup(this, y, x)
            popup.show()
        }

        upButton.setOnClickListener {
            if (y == 0){
                checkWrongRoad()
            }
            else if (y == 1 && x == 1){
                checkWrongRoad()
            }
            else if (y == 4 && x == 3){
                y -= 1
                x -= 1
                moveMap()
            }
            else if (y == 5 && x == 2){
                checkWrongRoad()
            }
            else if (y == 2 && x == 2){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    y -= 1
                    moveMap()
                }
                //todo 마이그레이션
            }
            else if (y == 4 && x == 0){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    y -= 1
                    moveMap()
                }
            }
            else if (y == 5 && x == 0){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    y -= 1
                    moveMap()
                }
            }
            else{
                y -= 1
                moveMap()
            }
        }
        downButton.setOnClickListener {
            if (y == 5){
                checkWrongRoad()
            }
            else if (y == 0 && x == 1){
                y += 1
                x += 1
                moveMap()
            }
            else if (y == 2 && x == 3){
                y += 1
                x -= 1
                moveMap()
            }
            else if (y == 3 && x == 2){
                y += 1
                x += 1
                moveMap()
            }
            else if (y == 4 && x == 3){
                checkWrongRoad()
            }
            else if (y == 5 && x == 2){
                checkWrongRoad()
            }
            else if (y == 1 && x == 2){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    y += 1
                    moveMap()
                }
            }
            else if (y == 3 && x == 0){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    y += 1
                    moveMap()
                }
            }
            else if (y == 4 && x == 0){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    y += 1
                    moveMap()
                }
            }
            else{
                y += 1
                moveMap()
            }
        }
        leftButton.setOnClickListener {
            if (x == 0){
                checkWrongRoad()
            }
            else if (y == 1 && x == 2){
                y -= 1
                x -= 1
                moveMap()
            }
            else if (y == 2 && x == 3){
                checkWrongRoad()
            }
            else if (y == 4 && x == 3){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    x -= 2
                    moveMap()
                }
            }
            else if (y == 5 && x == 1){
                checkWrongRoad()
            }
            else if (y == 2 && x == 2){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    x -= 1
                    moveMap()
                }
            }
            else{
                x -= 1
                moveMap()
            }
        }
        rightButton.setOnClickListener {
            if (x == 3){
                checkWrongRoad()
            }
            else if (y == 1 && x == 1){
                checkWrongRoad()
            }
            else if (y == 2 && x == 2){
                checkWrongRoad()
            }
            else if (y == 3 && x == 2){
                y -= 1
                x += 1
                moveMap()
            }
            else if (y == 4 && x == 1){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    x += 2
                    moveMap()
                }
            }
            else if (y == 5 && x == 0){
                checkWrongRoad()
            }
            else if (y == 5 && x == 2){
                checkWrongRoad()
            }
            else if (y == 2 && x == 1){
                setMonsterViewVisible(true)
                setControlViewVisible(false)
                monsterMove = {
                    x += 1
                    moveMap()
                }
            }
            else{
                x += 1
                moveMap()
            }
        }
        forestButton1.setOnClickListener {
            Toast.makeText(this, "이동력 1이 증가됩니다.", Toast.LENGTH_SHORT).show()
            moveLevel += 1
            moveLeveltext.setText(""+ moveLevel)
            checkTurn()
        }
        forestButton2.setOnClickListener {
            Toast.makeText(this, "공격력 1이 증가됩니다.", Toast.LENGTH_SHORT).show()
            damageLevel += 1
            damageLeveltext.setText(""+ damageLevel)
            checkTurn()
        }
        forestButton3.setOnClickListener {
            if (healLevel == 10){
                Toast.makeText(this, "체력이 이미 가득 차있습니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "체력 1이 증가됩니다.", Toast.LENGTH_SHORT).show()
                healLevel += 1
                healLeveltext.setText(""+ healLevel)
                checkTurn()
            }
        }
        // 몬스터 버튼
        monsterButton1.setOnClickListener {
            if (healLevel <= 1){
                endingGame(false)
            }
            else {
                healLevel -= 1
                setMonsterViewVisible(false)
                setControlViewVisible(true)
                monsterMove()
            }
        }
        monsterButton2.setOnClickListener {
            if (damageLevel == 0){
                Toast.makeText(this, "공격력이 0이므로 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                damageLevel -= 1
                setMonsterViewVisible(false)
                setControlViewVisible(true)
                monsterMove()
            }
        }
        // 다시하기 버튼 클릭 이벤트 설정
        tryAgainButton.setOnClickListener {
            resetGame() // 게임 다시 시작
        }

        // 게임 선택 화면으로 돌아가기 버튼 클릭 이벤트 설정
        goBackButton.setOnClickListener {
            var intent: Intent = Intent(this, MinigameSelect::class.java)
            startActivity(intent)
            finish()
        }

        val imageResId = when {
            MainScreen.character.color == "black" -> R.drawable.black_warrior
            MainScreen.character.color == "white" -> R.drawable.white_warrior
            MainScreen.character.color == "brown" -> R.drawable.brown_warrior
            else -> R.drawable.black_warrior
        }
        player.setImageResource(imageResId)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resetGame()
    }

    fun setForestViewVisible(isVisible: Boolean){
        //숲의 사원 관련 텍스트, 버튼 숨김
        if (isVisible == true){
            foresttext.visibility = View.VISIBLE
            forestButton1.visibility = View.VISIBLE
            forestButton2.visibility = View.VISIBLE
            forestButton3.visibility = View.VISIBLE
        }
        else{
            foresttext.visibility = View.INVISIBLE
            forestButton1.visibility = View.INVISIBLE
            forestButton2.visibility = View.INVISIBLE
            forestButton3.visibility = View.INVISIBLE
        }
    }

    fun setControlViewVisible(isVisible: Boolean){
        // UDRL 버튼 숨김, map 버튼 숨김
        if (isVisible){
            Log.d("force", "보이는거")
            upButton.visibility = View.VISIBLE
            downButton.visibility = View.VISIBLE
            leftButton.visibility = View.VISIBLE
            rightButton.visibility = View.VISIBLE
            mapButton.visibility = View.VISIBLE
        }
        else {
            Log.d("force", "안보이는거")
            upButton.visibility = View.INVISIBLE
            downButton.visibility = View.INVISIBLE
            leftButton.visibility = View.INVISIBLE
            rightButton.visibility = View.INVISIBLE
            mapButton.visibility = View.INVISIBLE
        }
    }

    fun setEndingViewVisible(isVisible: Boolean){
        if (isVisible){
            tryAgainButton.visibility = View.VISIBLE // 다시하기 버튼 보이기
            goBackButton.visibility = View.VISIBLE // 게임 선택 화면으로 돌아가기 버튼 보이기
            endingtext.visibility = View.VISIBLE // 엔딩 텍스트
        }
        else{
            tryAgainButton.visibility = View.INVISIBLE // 다시하기 버튼 보이기
            goBackButton.visibility = View.INVISIBLE // 게임 선택 화면으로 돌아가기 버튼 보이기
            endingtext.visibility = View.INVISIBLE // 엔딩 텍스트
        }
    }

    fun resetStatusText(){ // 스탯 현재 상태로 초기화
        moveLeveltext.setText(""+ moveLevel)
        damageLeveltext.setText(""+ damageLevel)
        groundtext.setText(groundArray[y][x])
        healLeveltext.setText(""+ healLevel)
        turnLeveltext.setText(""+ turnLevel)
        pointLeveltext.setText(""+ pointLevel)
    }

    fun setMonsterViewVisible(isVisible: Boolean){
        if (isVisible){
            monstertext.visibility = View.VISIBLE
            monsterButton1.visibility = View.VISIBLE
            monsterButton2.visibility = View.VISIBLE
        }
        else {
            monstertext.visibility = View.INVISIBLE
            monsterButton1.visibility = View.INVISIBLE
            monsterButton2.visibility = View.INVISIBLE
        }
    }


    fun changeTurn(){ // 턴 시작 시 이벤트
        turnLevel += 1 // 턴 증가 + 1

        moveLevel = moveLevelRandom.random()
        damageLevel = damageLevelRandom.random()
        //이동력 및 공격력 확률별로 부여
        if (moveLevel <= 7) moveLevel = 1
        if (moveLevel == 8 || moveLevel == 9) moveLevel = 2
        if (moveLevel == 10) moveLevel = 3
        if (damageLevel <= 5) damageLevel = 0
        if (damageLevel > 5 && damageLevel <= 9) damageLevel = 1
        if (damageLevel == 10) damageLevel = 2

        Toast.makeText(this, "이번턴 이동력 : " + moveLevel + " 공격력 : " + damageLevel, Toast.LENGTH_SHORT).show()

        turnRandomInfo = turnRandom.random()
        if (turnLevel > turnRandomInfo){
            dragonMode = 1 // 턴 습격
            startAttack()
        }

        resetStatusText()
    }

    fun checkTurn(){ // 턴을 체크 함수 (이동력이 0인지 아닌지)
        if (moveLevel <= 0){
            resetStatusText()
            changeTurn()
        }
        else{
            resetStatusText()
            setControlViewVisible(true)
            setForestViewVisible(false)
        }
    }

    fun checkAttack() : Boolean{ // 습격 회피 or 체력감소 확률 구현
        val hideCoin = Random.nextInt(1, 31) + dragonCoin

        if (hideCoin <= 30){ // 회피
            return false
        }
        else { // 체력 -1
            return true
        }
    }

    fun startAttack(){ // 네 번 습격 진행
        var attackMessage : String = ""
        when (dragonMode){
            1 -> attackMessage += "턴이 지나 습격이 진행됩니다!\n"
            2 -> attackMessage += "사원에서 습격이 진행됩니다!\n"
            3 -> attackMessage += "보물을 얻어 습격이 진행됩니다!\n"
        }
        for(i in 1..4){
            if (checkAttack()){
                attackMessage += "체력 1이 감소했습니다.\n"
                healLevel -= 1
                dragonCoin -= 1
                resetStatusText()
                if (healLevel < 1){
                    pointLevel = 0
                    resetStatusText()
                    endingGame(false)
                }
            }
            else{
                attackMessage += "회피했습니다.\n"
                resetStatusText()
            }
        }
        showAttackPopup(attackMessage)
    }

    fun showAttackPopup(message : String){ // 습격 텍스트 팝업
        val popup = AttackPopup(this, message, healLevel)
        popup.show()
    }

    fun endingGame(isSuccess : Boolean){ // 게임 엔딩
        if (isSuccess){
            exptext.visibility = View.VISIBLE
            endingtext.setText("클리어!\n점수 : " + pointLevel)

            if (pointLevel <= 10){ // 경험치 10
                MainScreen.character.gainExperience(10)
                exptext.setText("경험치를 10 획득했습니다!")
            }
            else if (pointLevel > 10 && pointLevel <= 20){ // 경험치 20, 알약 1
                MainScreen.character.gainExperience(20)
                MainScreen.character.medicine["pill"] = (MainScreen.character.medicine["pill"] ?: 0) + 1
                exptext.setText("경험치를 20 획득했습니다!\n알약 1개를 획득했습니다!")
            }
            else if (pointLevel > 20 && pointLevel <= 35){ // 경험치 30, 알약 2
                MainScreen.character.gainExperience(30)
                MainScreen.character.medicine["pill"] = (MainScreen.character.medicine["pill"] ?: 0) + 2
                exptext.setText("경험치를 30 획득했습니다!\n알약 2개를 획득했습니다!")
            }
            else if (pointLevel > 35 && pointLevel <= 45){ // 경험치 50, 알약 2, 주사기 1
                MainScreen.character.gainExperience(50)
                MainScreen.character.medicine["pill"] = (MainScreen.character.medicine["pill"] ?: 0) + 2
                MainScreen.character.medicine["pill"] = (MainScreen.character.medicine["injection"] ?: 0) + 1
                exptext.setText("경험치를 50 획득했습니다!\n알약 2개, 주사기 1개를 획득했습니다!")
            }
        }
        else{
            endingtext.setText("게임오버!\n점수 : " + pointLevel)
        }
        setForestViewVisible(false)
        setControlViewVisible(false)
        player.visibility = View.INVISIBLE
        setEndingViewVisible(true)
    }

    private fun resetGame(){ // 리셋버튼 눌렀을 시
        //값 초기화
        moveLevel = 0
        damageLevel = 0
        turnLevel = 0
        healLevel = 10
        pointLevel = 0
        x = 0
        y = 0
        hide = true
        isTreasureChest = false
        TreasureChest = arrayOf(false, false, false, false)
        dragonCoin = 0
        setForestViewVisible(false)
        setEndingViewVisible(false)
        setControlViewVisible(true)
        player.visibility = View.VISIBLE
        checkTurn()
    }

    private fun checkWrongRoad(){ // 잘못된 길 선택
        Toast.makeText(this, "길이 없습니다.", Toast.LENGTH_SHORT).show()
    }


    private fun moveMap(){ // 이동 성공 시
        moveLevel -= 1
        dragonCoin += 1
        resetStatusText()
        when(groundArray[y][x]){
            "평지" -> checkTurn()
            "수풀" -> {
                changeTurn()
            }
            "숲의 사원" -> {
                templeRandomInfo = templeRandom.random()
                if (templeRandomInfo < 4 + turnLevel){ //사원의 힘
                    Log.d("force", "사원의 힘")
                    setControlViewVisible(false)
                    setForestViewVisible(true)
                }
                else { // 습격
                    Log.d("attack", "사원 습격")
                    dragonMode = 2 // 사원 습격
                    startAttack()
                }
                checkTurn()
            }
            "시작점" -> {
                if (isTreasureChest == true){
                    endingGame(true)
                }
                else{
                    checkTurn()
                }
            }
            else -> checkTreasureChest() // 보물상자
        }
    }


    private fun checkTreasureChest(){ // 보물상자 체크 (중복 획득 금지)
        dragonMode = 3 // 보물상자 습격
        isTreasureChest = true
        when(groundArray[y][x]){
            "보물상자A" -> {
                if (TreasureChest[0] == false) {
                    startAttack()
                    pointLevel += 5
                    TreasureChest[0] = true
                }
                checkTurn()
            }
            "보물상자B" -> {
                if (TreasureChest[1] == false) {
                    startAttack()
                    pointLevel += 15
                    TreasureChest[1] = true
                }
                checkTurn()
            }
            "보물상자C" -> {
                if (TreasureChest[2] == false) {
                    startAttack()
                    pointLevel += 10
                    TreasureChest[2] = true
                }
                checkTurn()
            }
            "보물상자D" -> {
                if (TreasureChest[3] == false) {
                    startAttack()
                    pointLevel += 15
                    TreasureChest[3] = true
                }
                checkTurn()
            }
        }
    }


}