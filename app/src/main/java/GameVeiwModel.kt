package com.example.assignment1  // 프로젝트 패키지명

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    // 틱택토 보드를 나타내는 2차원 배열을 LiveData로 선언
    private val _board = MutableLiveData(Array(3) { arrayOfNulls<String>(3) })
    val board: LiveData<Array<Array<String?>>> = _board  // 외부에 읽기만 가능한 보드 상태 제공

    // 현재 플레이어를 저장하는 변수
    private val _currentPlayer = MutableLiveData("O")
    val currentPlayer: LiveData<String> = _currentPlayer

    // 게임 상태 메시지 ("O의 차례입니다", "X가 승리했습니다" 등)
    private val _gameStatus = MutableLiveData("O의 차례입니다")
    val gameStatus: LiveData<String> = _gameStatus

    // 클릭 시 호출되는 함수: 보드에 움직임 반영
    fun makeMove(row: Int, col: Int) {
        if (_board.value?.get(row)?.get(col) == null) {
            val newBoard = _board.value?.copyOf()  // 기존 보드를 복사
            newBoard?.get(row)?.set(col, _currentPlayer.value)  // 현재 플레이어의 기호(X/O)를 보드에 반영
            _board.value = newBoard  // 업데이트된 보드 상태 적용

            // 승리 여부 체크
            if (checkWinner()) {
                _gameStatus.value = "${_currentPlayer.value}가 승리했습니다!"
            } else {
                // 차례 변경: 현재 차례가 "O"면 "X", "X"면 "O"로 전환
                _currentPlayer.value = if (_currentPlayer.value == "O") "X" else "O"
                _gameStatus.value = "${_currentPlayer.value}의 차례입니다"
            }
        }
    }

    // 승리 여부를 확인하는 함수
    private fun checkWinner(): Boolean {
        val b = _board.value ?: return false
        for (i in 0..2) {
            if (b[i][0] != null && b[i][0] == b[i][1] && b[i][1] == b[i][2]) return true  // 가로 체크
            if (b[0][i] != null && b[0][i] == b[1][i] && b[1][i] == b[2][i]) return true  // 세로 체크
        }
        if (b[0][0] != null && b[0][0] == b[1][1] && b[1][1] == b[2][2]) return true  // 대각선 체크
        if (b[0][2] != null && b[0][2] == b[1][1] && b[1][1] == b[2][0]) return true  // 반대 대각선 체크
        return false
    }

    // 게임을 리셋하는 함수: 초기 상태로 되돌림
    fun resetGame() {
        _board.value = Array(3) { arrayOfNulls<String>(3) }  // 빈 보드로 초기화
        _currentPlayer.value = "O"  // O부터 시작
        _gameStatus.value = "O의 차례입니다"  // 상태 메시지 초기화
    }
}
