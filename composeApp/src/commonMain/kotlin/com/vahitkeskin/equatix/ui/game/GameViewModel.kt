package com.vahitkeskin.equatix.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.vahitkeskin.equatix.data.local.createDataStore
import com.vahitkeskin.equatix.di.AppModule
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

class GameViewModel : ScreenModel {

    // --- REPOSITORIES ---
    // Tema ve Ayarları okumak için
    private val settingsRepo = AppModule.settingsRepository

    // --- SETTINGS STATES (Flow) ---
    val themeConfig: StateFlow<AppThemeConfig> = settingsRepo.themeConfig
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeConfig.FOLLOW_SYSTEM
        )

    // --- GAME STATES ---
    var gameState by mutableStateOf<GameState?>(null)
        private set
    var isSolved by mutableStateOf(false)
        private set
    var isSurrendered by mutableStateOf(false)
        private set
    var selectedCellIndex by mutableStateOf<Int?>(null)
        private set
    var isVibrationEnabled by mutableStateOf(true)
        private set

    // Restart için mevcut ayarları tutmak faydalı olabilir
    var currentDifficulty: Difficulty = Difficulty.EASY
        private set
    var currentSize: GridSize = GridSize.SIZE_3x3
        private set

    // --- ACTIONS ---

    fun toggleVibration() {
        isVibrationEnabled = !isVibrationEnabled
    }

    fun startGame(difficulty: Difficulty, size: GridSize) {
        currentDifficulty = difficulty
        currentSize = size
        isSolved = false
        isSurrendered = false
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

    // --- GAME GENERATION LOGIC ---

    private fun generateSolvableLevel(diff: Difficulty, n: Int): GameState {
        while (true) {
            val totalCells = n * n
            val nums = List(totalCells) { Random.nextInt(1, diff.maxNumber + 1) }

            val opCountPerDim = n * (n - 1)

            val availableOps = when(diff) {
                Difficulty.EASY -> listOf(Operation.ADD, Operation.SUB)
                Difficulty.MEDIUM -> listOf(Operation.ADD, Operation.SUB, Operation.MUL)
                Difficulty.HARD -> listOf(Operation.ADD, Operation.SUB, Operation.MUL, Operation.DIV)
            }

            val rOps = List(opCountPerDim) { availableOps.random() }
            val cOps = List(opCountPerDim) { availableOps.random() }

            val rowResults = mutableListOf<Int>()
            val colResults = mutableListOf<Int>()
            var isValidLevel = true

            // 1. Satır Hesapla
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

            // 2. Sütun Hesapla
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

    // --- HELPERS ---

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

    fun onCellSelected(index: Int) {
        if (gameState?.grid?.get(index)?.isLocked == false && !isSolved) selectedCellIndex = index
    }

    fun onInput(key: String) {
        val currentIndex = selectedCellIndex ?: return
        val currentGameState = gameState ?: return
        if (isSolved) return
        val newGrid = currentGameState.grid.toMutableList()
        val currentCell = newGrid[currentIndex]
        val oldVal = currentCell.userInput
        val newVal = when (key) {
            "DEL" -> if (oldVal.isNotEmpty()) oldVal.dropLast(1) else ""
            else -> if (oldVal.length < 3) oldVal + key else oldVal
        }
        newGrid[currentIndex] = currentCell.copy(userInput = newVal)
        gameState = currentGameState.copy(grid = newGrid)
        checkWin()
    }

    private fun checkWin() {
        val allCorrect =
            gameState?.grid?.all { if (!it.isHidden) true else it.userInput.toIntOrNull() == it.correctValue }
                ?: false
        if (allCorrect) {
            isSolved = true; selectedCellIndex = null
        }
    }

    fun loadPreviewState(customState: GameState) {
        this.gameState = customState
        this.isSolved = true
    }
}