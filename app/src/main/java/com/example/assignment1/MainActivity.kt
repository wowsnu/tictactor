package com.example.assignment1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.Observer
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment1.GameViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var gridLayout: GridLayout
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.status_text)
        gridLayout = findViewById(R.id.gridLayout)

        // UI 업데이트: 게임 상태를 관찰하고 변화에 따라 UI를 변경
        viewModel.gameStatus.observe(this, Observer { status ->
            statusText.text = status
        })

        viewModel.board.observe(this, Observer { board ->
            updateBoardUI(board)
        })

        val resetButton = findViewById<Button>(R.id.reset_button)  // reset_button은 XML에서 정의된 ID
        resetButton.setOnClickListener {
            viewModel.resetGame()  // ViewModel의 resetGame() 호출
        }

        initializeButtons()
    }

    private fun initializeButtons() {
        // GridLayout에서 버튼 초기화
        for (row in 0 until gridLayout.rowCount) {
            for (col in 0 until gridLayout.columnCount) {
                val button = gridLayout.getChildAt(row * gridLayout.columnCount + col) as Button
                button.setOnClickListener {
                    viewModel.makeMove(row, col)
                }
            }
        }
    }

    private fun updateBoardUI(board: Array<Array<String?>>) {
        // GridLayout의 각 버튼을 보드 상태에 맞게 업데이트
        for (row in 0 until gridLayout.rowCount) {
            for (col in 0 until gridLayout.columnCount) {
                val button = gridLayout.getChildAt(row * gridLayout.columnCount + col) as Button
                button.text = board[row][col] ?: ""
            }
        }
    }
}
