package com.example.assignment1.com.example.assignment1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.databinding.BoardHistoryBinding
import com.example.assignment1.databinding.LastBoardBinding
import com.example.assignment1.databinding.StartGameItemBinding


class DrawerViewTypeAdapter(
    private val items: List<DrawerItem>,
    private val onStartGameClick: () -> Unit,
    private val onBoardItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_START_BUTTON = 0
        private const val TYPE_BOARD = 1
        private const val TYPE_LAST_BOARD = 2
    }
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DrawerItem.StartButtonItem -> TYPE_START_BUTTON
            is DrawerItem.BoardItem -> TYPE_BOARD
            is DrawerItem.LastBoardItem -> TYPE_LAST_BOARD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_START_BUTTON -> {
                val binding = StartGameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StartGameViewHolder(binding)
            }
            TYPE_BOARD -> {
                val binding = BoardHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                BoardViewHolder(binding)
            }
            TYPE_LAST_BOARD -> {
                val binding = LastBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LastBoardViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid View Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StartGameViewHolder -> holder.bind(onStartGameClick)
            is BoardViewHolder -> holder.bind(items[position] as DrawerItem.BoardItem, onBoardItemClick)
            is LastBoardViewHolder -> holder.bind(items[position] as DrawerItem.LastBoardItem)
        }
    }

    class StartGameViewHolder(private val binding: StartGameItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(onStartGameClick: () -> Unit) {
            binding.startGameButton.setOnClickListener { onStartGameClick() }
        }
    }

    class BoardViewHolder(private val binding: BoardHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(boardItem: DrawerItem.BoardItem, onBoardItemClick: (Int) -> Unit) {
            binding.moveNumber.text = "${boardItem.moveNumber} 턴"

            val cellIds = arrayOf(
                binding.cell00, binding.cell01, binding.cell02,
                binding.cell10, binding.cell11, binding.cell12,
                binding.cell20, binding.cell21, binding.cell22
            )

            for (i in boardItem.board.indices) {
                for (j in boardItem.board[i].indices) {
                    cellIds[i * 3 + j].text = boardItem.board[i][j] ?: ""
                }
            }

            binding.gotoButton.setOnClickListener { onBoardItemClick(boardItem.moveNumber) }
        }
    }

    class LastBoardViewHolder(private val binding: LastBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(boardItem: DrawerItem.LastBoardItem) {

            binding.stateText.text = boardItem.gameStatus

            val cellIds = arrayOf(
                binding.cell00, binding.cell01, binding.cell02,
                binding.cell10, binding.cell11, binding.cell12,
                binding.cell20, binding.cell21, binding.cell22
            )

            for (i in boardItem.board.indices) {
                for (j in boardItem.board[i].indices) {
                    cellIds[i * 3 + j].text = boardItem.board[i][j] ?: ""
                }
            }
        }
    }

    override fun getItemCount() = items.size
}