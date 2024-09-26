package com.example.assignment1  // 프로젝트 패키지명

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    // 틱택토 보드의 history를 포함하는 3차원 배열
    private val _boardHistory = MutableLiveData(mutableListOf(Array(3) { arrayOfNulls<String>(3) }))
    val boardHistory: MutableLiveData<MutableList<Array<Array<String?>>>> = _boardHistory

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    // 현재 플레이어를 저장하는 변수
    private val _currentPlayer = MutableLiveData("O")
    val currentPlayer: LiveData<String> = _currentPlayer

    // 게임 상태 메시지 ("O의 차례입니다", "X가 승리했습니다" 등)
    private val _gameStatus = MutableLiveData("O의 차례입니다")
    val gameStatus: LiveData<String> = _gameStatus

    // 클릭 시 호출되는 함수: 보드에 움직임 반영
    fun makeMove(row: Int, col: Int) {
        val currentBoard = _boardHistory.value?.getOrNull(_currentIndex.value ?: 0) ?: return
        if (currentBoard[row][col] == null) {
            val newBoard = currentBoard.map { it.copyOf() }.toTypedArray()  // 기존 보드를 복사
            newBoard[row][col] = _currentPlayer.value // 현재 플레이어의 기호(X/O)를 보드에 반영

            val newHistory = _boardHistory.value?.take((_currentIndex.value ?: 0) + 1)?.toMutableList() ?: mutableListOf()
            newHistory.add(newBoard)
            _boardHistory.value = newHistory
            _currentIndex.value = newHistory.size - 1
            // 승리 여부 체크
            if (checkWinner(newBoard)) {
                _gameStatus.value = "${_currentPlayer.value}가 승리했습니다!"
            } else {
                // 차례 변경: 현재 차례가 "O"면 "X", "X"면 "O"로 전환
                _currentPlayer.value = if (_currentPlayer.value == "O") "X" else "O"
                _gameStatus.value = "${_currentPlayer.value}의 차례입니다"
            }
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

    // 게임을 리셋하는 함수: 초기 상태로 되돌림
    fun resetGame() {
        _boardHistory.value = mutableListOf(Array(3) { arrayOfNulls<String>(3) })
        _currentIndex.value = 0
        _currentPlayer.value = "O"  // O부터 시작
        _gameStatus.value = "O의 차례입니다"  // 상태 메시지 초기화
    }

    fun getCurrentBoard(): Array<Array<String?>> {
        return boardHistory.value?.getOrNull(currentIndex.value ?: 0)
            ?: Array(3) { arrayOfNulls(3) }
    }
}
