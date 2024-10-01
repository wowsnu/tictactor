package com.example.assignment1

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment1.com.example.assignment1.DrawerItem
import com.example.assignment1.com.example.assignment1.DrawerViewTypeAdapter
import com.example.assignment1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGameStatusObserver()
        setupResetButton()
        initializeButtons()
        setupDrawer()
        setupBoardObserver()
        setupGameOverObserver()
        setupDrawerRecyclerView()
    }

    private fun setupGameStatusObserver() {
        viewModel.gameStatusDisplay.observe(this) { status ->
            binding.statusText.text = status
        }
    }

    private fun setupResetButton() {
        binding.resetButton.setOnClickListener {
            viewModel.resetGame()
            updateBoardUI(viewModel.getCurrentBoard())
        }
    }

    private fun setupDrawer() {
        binding.drawerButton.setOnClickListener {
            if (binding.main.isDrawerOpen(GravityCompat.START)) {
                binding.main.closeDrawer(GravityCompat.START)
            } else {
                binding.main.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupBoardObserver() {
        viewModel.boardHistory.observe(this) { history ->
            viewModel.currentIndex.value?.let { currentIndex ->
                if (currentIndex >= 0 && currentIndex < history.size) {
                    updateBoardUI(history[currentIndex])
                }
            }
        }
    }

    private fun setupGameOverObserver() {
        viewModel.isGameOver.observe(this) { isGameOver ->
            for (row in 0 until binding.gridLayout.rowCount) {
                for (col in 0 until binding.gridLayout.columnCount) {
                    val button = binding.gridLayout.getChildAt(row * binding.gridLayout.columnCount + col) as Button
                    button.isEnabled = !isGameOver
                }
            }
            binding.resetButton.text = viewModel.getResetButtonText()
        }
    }

    private fun initializeButtons() {
        // GridLayout에서 버튼 초기화
        for (row in 0 until binding.gridLayout.rowCount) {
            for (col in 0 until binding.gridLayout.columnCount) {
                val button = binding.gridLayout.getChildAt(row * binding.gridLayout.columnCount + col) as Button
                button.setOnClickListener {
                    viewModel.makeMove(row, col)
                    updateBoardUI(viewModel.getCurrentBoard())
                }
            }
        }
    }

    private fun updateBoardUI(board: List<List<String?>>) {
        // GridLayout의 각 버튼을 보드 상태에 맞게 업데이트
        for (row in 0 until binding.gridLayout.rowCount) {
            for (col in 0 until binding.gridLayout.columnCount) {
                val button = binding.gridLayout.getChildAt(row * binding.gridLayout.columnCount + col) as Button
                button.text = board[row][col] ?: ""
            }
        }
    }
    private fun setupDrawerRecyclerView() {
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        val drawerItems = mutableListOf<DrawerItem>()

        fun updateDrawerItems() {
            val history = viewModel.boardHistory.value ?: return
            val isGameOver = viewModel.isGameOver.value ?: false
            val gameStatus = viewModel.gameStatusDisplay.value ?: ""

            drawerItems.clear()
            drawerItems.add(DrawerItem.StartButtonItem)
            history.forEachIndexed { index, board ->
                if (index != 0) {
                    if (index == history.size - 1 && isGameOver) {
                        drawerItems.add(DrawerItem.LastBoardItem(board, gameStatus))
                    } else {
                        drawerItems.add(DrawerItem.BoardItem(index, board))
                    }
                }
            }

            binding.historyRecyclerView.adapter = DrawerViewTypeAdapter(
                items = drawerItems,
                onStartGameClick = {
                    viewModel.resetGame()
                    binding.main.closeDrawer(GravityCompat.START)
                },
                onBoardItemClick = { moveNumber ->
                    viewModel.goToMove(moveNumber)
                    binding.main.closeDrawer(GravityCompat.START)
                }
            )
        }

        viewModel.boardHistory.observe(this) { _ -> updateDrawerItems() }
        viewModel.isGameOver.observe(this) { _ -> updateDrawerItems() }
        viewModel.gameStatusDisplay.observe(this) { _ -> updateDrawerItems() }        }
    }
