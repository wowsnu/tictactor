package com.example.assignment1  // 프로젝트 패키지명

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    // 틱택토 보드의 history를 포함하는 3차원 배열
    private val _boardHistory = MutableLiveData(mutableListOf(Array(3) { arrayOfNulls<String>(3) }))
    val boardHistory: MutableLiveData<MutableList<Array<Array<String?>>>> = _boardHistory

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _isGameOver = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> = _isGameOver

    // 게임 상태 메시지 ("O의 차례입니다", "X가 승리했습니다" 등)
    private val _gameStatusDisplay = MutableLiveData("O의 차례입니다")
    val gameStatusDisplay: LiveData<String> = _gameStatusDisplay

    private val _resetButtonText = MutableLiveData("초기화")
    val resetButtonText: LiveData<String> = _resetButtonText

    // 클릭 시 호출되는 함수: 보드에 움직임 반영
    fun makeMove(row: Int, col: Int) {
        val currentBoard = getCurrentBoard()
        if (currentBoard[row][col] == null) {
            val newBoard = currentBoard.map { it.copyOf() }.toTypedArray()  // 기존 보드를 복사
            val currentPlayer = getCurrentPlayer()
            newBoard[row][col] = currentPlayer // 현재 플레이어의 기호(X/O)를 보드에 반영
            val newHistory = _boardHistory.value?.take((_currentIndex.value ?: 0) + 1)?.toMutableList() ?: mutableListOf()
            newHistory.add(newBoard)
            _boardHistory.value = newHistory
            _currentIndex.value = newHistory.size - 1

            updateGameStatus()
        }
    }

    // 승리 여부를 확인하는 함수
    private fun checkWinner(board: Array<Array<String?>>): Boolean {
        for (i in 0..2) {
            if (board[i][0] != null && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return true  // 가로 체크
            if (board[0][i] != null && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return true  // 세로 체크
        }
        if (board[0][0] != null && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return true  // 대각선 체크
        if (board[0][2] != null && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return true  // 반대 대각선 체크
        return false
    }

    private fun isBoardFull(): Boolean {
        return getCurrentBoard().all { row -> row.all { it != null } }
    }

    // 게임을 리셋하는 함수: 초기 상태로 되돌림
    fun resetGame() {
        _boardHistory.value = mutableListOf(Array(3) { arrayOfNulls(3) })
        _currentIndex.value = 0
        updateGameStatus()
        _isGameOver.value = false
        _resetButtonText.value = "초기화"
    }

    fun getCurrentBoard(): Array<Array<String?>> {
        return boardHistory.value?.getOrNull(currentIndex.value ?: 0)
            ?: Array(3) { arrayOfNulls(3) }
    }

    private fun getCurrentPlayer(): String {
        return if (_currentIndex.value!! % 2 == 0) "O" else "X"
    }

    private fun getOpponentPlayer(): String {
        return if (getCurrentPlayer() == "O") "X" else "O"
    }

    fun updateGameStatus() {
        val currentBoard = getCurrentBoard()
        // 승리 여부 체크
        if (checkWinner(currentBoard)) {
            _gameStatusDisplay.value = "${getOpponentPlayer()}가 승리했습니다!"
            endGame()
            return
        }
        if (isBoardFull()) {
            _gameStatusDisplay.value = "무승부!"
            endGame()
            return
        }
            // 차례 변경: 현재 차례가 "O"면 "X", "X"면 "O"로 전환
        _gameStatusDisplay.value = "${getCurrentPlayer()}의 차례입니다"
    }
    
    private fun endGame() {
        _isGameOver.value = true
        _resetButtonText.value = "한판 더"
    }

    fun goToMove(position: Int) {
        if (position >= 0 && position < (_boardHistory.value?.size ?: 0)) {
            _currentIndex.value = position
            _boardHistory.value = _boardHistory.value?.take(position + 1)?.toMutableList()
            _isGameOver.value = false
            updateGameStatus()
        }

    }
}
