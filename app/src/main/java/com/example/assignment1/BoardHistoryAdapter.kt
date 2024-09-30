package com.example.assignment1.com.example.assignment1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.databinding.BoardHistoryBinding
import com.example.assignment1.databinding.StartGameItemBinding


class BoardHistoryAdapter(
    private val history: List<Array<Array<String?>>>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_START_BUTTON = 0
        private const val TYPE_BOARD = 1
    }
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_START_BUTTON
            else -> TYPE_BOARD
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
            else -> throw IllegalArgumentException("Invalid View Type")
        }
    }

    class StartGameViewHolder(private val binding: StartGameItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(onItemClick: () -> Unit) {
            binding.startGameButton.setOnClickListener { onItemClick() }
        }
    }

    class BoardViewHolder(private val binding: BoardHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(moveNumber: Int, board: Array<Array<String?>>, onItemClick: (Int) -> Unit) {
            binding.moveNumber.text = "Move $moveNumber"

            val cellIds = arrayOf(
                binding.cell00, binding.cell01, binding.cell02,
                binding.cell10, binding.cell11, binding.cell12,
                binding.cell20, binding.cell21, binding.cell22
            )

            for (i in board.indices) {
                for (j in board[i].indices) {
                    cellIds[i * 3 + j].text = board[i][j] ?: ""
                }
            }

            binding.gotoButton.setOnClickListener { onItemClick(adapterPosition - 1) }
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StartGameViewHolder -> holder.bind {onItemClick(-1) }
            is BoardViewHolder -> holder.bind(position, history[position - 1], onItemClick)
        }
    }


    override fun getItemCount() = history.size + 1
}