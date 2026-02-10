package com.vahitkeskin.equatix.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.vahitkeskin.equatix.di.AppModule
import com.vahitkeskin.equatix.domain.model.AppDictionary
import com.vahitkeskin.equatix.domain.model.AppLanguage
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.platform.AdActions
import com.vahitkeskin.equatix.platform.KeyValueStorage
import com.vahitkeskin.equatix.ui.game.utils.GameUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ScreenModel {

    // --- REPOSITORIES & STORAGE ---
    private val settingsRepo = AppModule.settingsRepository
    private val scoreRepo = AppModule.scoreRepository
    private val storage = KeyValueStorage()

    // Dil anahtarı HomeViewModel ile aynı olmalı
    private val LANGUAGE_KEY = "selected_language"

    // --- DİL DESTEĞİ (YENİ) ---
    private val _strings = MutableStateFlow(AppDictionary.en)
    val strings = _strings.asStateFlow()

    // --- TUTORIAL & VIBRATION STATE ---
    val isTutorialSeen = settingsRepo.isTutorialSeen
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // Titreşim ayarını repo'dan dinliyoruz (StateFlow olarak)
    val isVibrationEnabled = settingsRepo.isVibrationOn
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // --- UI EVENTS ---
    private val _uiEvent = Channel<GameUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // --- GAME STATES ---
    var gameState by mutableStateOf<GameState?>(null)
        private set
    var isSolved by mutableStateOf(false)
        private set
    var isSurrendered by mutableStateOf(false)
        private set
    var selectedCellIndex by mutableStateOf<Int?>(null)
        private set

    // --- DIALOG STATES ---
    var showWinDialog by mutableStateOf(false)
        private set
    var lastGameScore by mutableStateOf(0)
        private set

    // Restart için mevcut ayarlar
    var currentDifficulty: Difficulty = Difficulty.EASY
        private set
    var currentSize: GridSize = GridSize.SIZE_3x3
        private set

    init {
        // ViewModel oluştuğunda doğru dili yükle
        loadSavedLanguage()
    }

    // HomeViewModel'deki mantığın aynısı: Kaydedilen dili bul ve stringleri güncelle
    private fun loadSavedLanguage() {
        val savedCode = storage.getString(LANGUAGE_KEY)

        val savedLanguage = if (savedCode != null) {
            AppLanguage.values().find { it.code == savedCode } ?: AppLanguage.SYSTEM
        } else {
            AppLanguage.SYSTEM
        }

        val languageToLoad = if (savedLanguage == AppLanguage.SYSTEM) {
            AppLanguage.getDeviceLanguage()
        } else {
            savedLanguage
        }

        _strings.value = AppDictionary.getStrings(languageToLoad)
    }

    // Tutorial bittiğinde çağıracağız
    fun markTutorialAsSeen() {
        screenModelScope.launch {
            settingsRepo.setTutorialSeen()
        }
    }

    // --- ACTIONS ---

    // Artık global ayarı değiştiriyor (Sadece bu ekranı değil)
    fun toggleVibration() {
        screenModelScope.launch {
            settingsRepo.setVibration(!isVibrationEnabled.value)
        }
    }

    fun startGame(difficulty: Difficulty, size: GridSize) {
        currentDifficulty = difficulty
        currentSize = size
        isSolved = false
        isSurrendered = false
        showWinDialog = false
        selectedCellIndex = null
        gameState = generateSolvableLevel(difficulty, size.value)
    }

    fun giveUpAndSolve() {
        val currentState = gameState ?: return
        val solvedGrid = currentState.grid.map { cell ->
            if (cell.isHidden) cell.copy(
                userInput = cell.correctValue.toString(),
                isRevealedBySystem = true
            ) else cell
        }
        gameState = currentState.copy(grid = solvedGrid)
        isSolved = true
        isSurrendered = true
        selectedCellIndex = null
    }

    fun revealOneHint() {
        val currentState = gameState ?: return
        val hiddenCells = currentState.grid.filter { it.isHidden && it.userInput != it.correctValue.toString() }
        if (hiddenCells.isEmpty()) return

        val cellToReveal = hiddenCells.random()
        val updatedGrid = currentState.grid.map { cell ->
            if (cell.id == cellToReveal.id) {
                cell.copy(userInput = cell.correctValue.toString(), isRevealedBySystem = true)
            } else cell
        }
        gameState = currentState.copy(grid = updatedGrid)
        checkWin()
    }

    fun onHintClick() {
        AdActions.showRewarded {
            revealOneHint()
        }
    }

    fun dismissWinDialog() {
        showWinDialog = false
    }

    // --- INPUT LOGIC ---

    fun onCellSelected(index: Int) {
        if (gameState?.grid?.get(index)?.isLocked == false && !isSolved) selectedCellIndex = index
    }

    fun onInput(key: String) {
        val currentIndex = selectedCellIndex ?: return
        val currentGameState = gameState ?: return

        if (isSolved || isSurrendered) return

        val currentGrid = currentGameState.grid.toMutableList()
        val currentCell = currentGrid[currentIndex]

        if (currentCell.isLocked) return

        if (key == "DEL") {
            if (currentCell.userInput.isNotEmpty()) {
                val newVal = currentCell.userInput.dropLast(1)
                val updatedCell = currentCell.copy(userInput = newVal)
                currentGrid[currentIndex] = updatedCell
                gameState = currentGameState.copy(grid = currentGrid)
            }
        } else {
            val currentInput = currentCell.userInput
            // Max 3 hane sınırı
            if (currentInput.length >= 3) {
                triggerErrorHaptic()
                return
            }

            val candidateInput = currentInput + key
            val correctValueStr = currentCell.correctValue.toString()

            if (correctValueStr.startsWith(candidateInput)) {
                val updatedCell = currentCell.copy(userInput = candidateInput)
                currentGrid[currentIndex] = updatedCell
                gameState = currentGameState.copy(grid = currentGrid)

                if (candidateInput == correctValueStr) {
                    checkWin()
                }
            } else {
                triggerErrorHaptic()
            }
        }
    }

    private fun triggerErrorHaptic() {
        // Repo'dan gelen değeri kontrol et
        if (isVibrationEnabled.value) {
            screenModelScope.launch {
                _uiEvent.send(GameUiEvent.VibrateError)
            }
        }
    }

    private fun checkWin() {
        val allCorrect = gameState?.grid?.all {
            if (!it.isHidden) true else it.userInput.toIntOrNull() == it.correctValue
        } ?: false

        if (allCorrect) {
            isSolved = true
            selectedCellIndex = null
            
            // Kazanma durumunda Interstitial reklam göster
            AdActions.showInterstitial { }
        }
    }

    fun onGameFinished(finalTimeSeconds: Long) {
        if (isSurrendered || gameState == null) return

        screenModelScope.launch {
            val state = gameState!!
            val finalScore = calculateScore(state.difficulty, state.size, finalTimeSeconds)

            lastGameScore = finalScore
            showWinDialog = true

            val gridSizeEnum = when (state.size) {
                3 -> GridSize.SIZE_3x3
                4 -> GridSize.SIZE_4x4
                5 -> GridSize.SIZE_5x5
                else -> GridSize.SIZE_3x3
            }

            scoreRepo.saveScore(
                score = finalScore,
                timeSeconds = finalTimeSeconds,
                difficulty = state.difficulty,
                gridSize = gridSizeEnum
            )
        }
    }

    // --- GAME GENERATION LOGIC ---
    // (Bu kısımlar değişmedi, aynen korundu)

    private fun generateSolvableLevel(diff: Difficulty, n: Int): GameState {
        while (true) {
            val totalCells = n * n
            val nums = List(totalCells) { Random.nextInt(1, diff.maxNumber + 1) }

            val opCountPerDim = n * (n - 1)

            val availableOps = when (diff) {
                Difficulty.EASY -> listOf(Operation.ADD, Operation.SUB)
                Difficulty.MEDIUM -> listOf(Operation.ADD, Operation.SUB, Operation.MUL)
                Difficulty.HARD -> listOf(
                    Operation.ADD,
                    Operation.SUB,
                    Operation.MUL,
                    Operation.DIV
                )
            }

            val rOps = List(opCountPerDim) { availableOps.random() }
            val cOps = List(opCountPerDim) { availableOps.random() }

            val rowResults = mutableListOf<Int>()
            val colResults = mutableListOf<Int>()
            var isValidLevel = true

            // Satır
            for (row in 0 until n) {
                val rowNums = nums.subList(row * n, (row + 1) * n)
                val rowOpList = rOps.subList(row * (n - 1), (row + 1) * (n - 1))

                val result = calculateLineWithPrecedence(rowNums, rowOpList)
                if (result == null) {
                    isValidLevel = false
                    break
                }
                rowResults.add(result)
            }

            if (!isValidLevel) continue

            // Sütun
            for (col in 0 until n) {
                val colNums = (0 until n).map { row -> nums[row * n + col] }
                val colOpList = (0 until n - 1).map { k -> cOps[col * (n - 1) + k] }

                val result = calculateLineWithPrecedence(colNums, colOpList)
                if (result == null) {
                    isValidLevel = false
                    break
                }
                colResults.add(result)
            }

            if (!isValidLevel) continue

            val hiddenMap = determineHiddenCells(diff, n)
            val cells = nums.mapIndexed { index, value ->
                val hide = hiddenMap[index]
                CellData(
                    id = index,
                    correctValue = value,
                    isHidden = hide,
                    userInput = if (hide) "" else value.toString(),
                    isLocked = !hide
                )
            }

            return GameState(n, cells, rOps, cOps, rowResults, colResults, diff)
        }
    }

    private fun calculateLineWithPrecedence(numbers: List<Int>, operations: List<Operation>): Int? {
        val tempNums = numbers.toMutableList()
        val tempOps = operations.toMutableList()

        var i = 0
        while (i < tempOps.size) {
            val op = tempOps[i]
            if (op == Operation.MUL || op == Operation.DIV) {
                val n1 = tempNums[i]
                val n2 = tempNums[i + 1]

                val result = if (op == Operation.MUL) {
                    n1 * n2
                } else {
                    if (n2 == 0 || n1 % n2 != 0) return null
                    n1 / n2
                }

                tempNums[i] = result
                tempNums.removeAt(i + 1)
                tempOps.removeAt(i)
            } else {
                i++
            }
        }

        var finalResult = tempNums[0]
        for (j in tempOps.indices) {
            val nextNum = tempNums[j + 1]
            finalResult = when (tempOps[j]) {
                Operation.ADD -> finalResult + nextNum
                Operation.SUB -> finalResult - nextNum
                else -> finalResult
            }
        }
        return finalResult
    }

    private fun determineHiddenCells(diff: Difficulty, n: Int): BooleanArray {
        val totalCells = n * n
        val hiddenState = BooleanArray(totalCells) { false }
        val indices = (0 until totalCells).toList().shuffled()

        val ratio = when (diff) {
            Difficulty.EASY -> 0.30f
            Difficulty.MEDIUM -> 0.45f
            Difficulty.HARD -> 0.60f
        }
        val targetHidden = (totalCells * ratio).toInt().coerceAtLeast(1)
        var currentHiddenCount = 0

        for (i in indices) {
            if (currentHiddenCount >= targetHidden) break
            hiddenState[i] = true
            if (isSolvable(hiddenState, n)) currentHiddenCount++ else hiddenState[i] = false
        }
        return hiddenState
    }

    private fun isSolvable(hiddenMap: BooleanArray, n: Int): Boolean {
        val unknownMap = hiddenMap.toMutableList()
        var progress = true
        while (progress) {
            progress = false
            for (r in 0 until n) {
                val unknownsInRow = (0 until n).map { c -> r * n + c }.filter { unknownMap[it] }
                if (unknownsInRow.size == 1) {
                    unknownMap[unknownsInRow[0]] = false; progress = true
                }
            }
            for (c in 0 until n) {
                val unknownsInCol = (0 until n).map { r -> r * n + c }.filter { unknownMap[it] }
                if (unknownsInCol.size == 1) {
                    unknownMap[unknownsInCol[0]] = false; progress = true
                }
            }
        }
        return !unknownMap.contains(true)
    }

    private fun calculateScore(difficulty: Difficulty, gridSize: Int, timeSeconds: Long): Int {
        val diffMultiplier = when (difficulty) {
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 2
            Difficulty.HARD -> 3
        }

        val baseScore = 1000 * diffMultiplier * gridSize
        val timePenalty = (timeSeconds * 2).toInt()

        return (baseScore - timePenalty).coerceAtLeast(100)
    }

    fun loadPreviewState(customState: GameState) {
        this.gameState = customState
        this.isSolved = true
    }
}