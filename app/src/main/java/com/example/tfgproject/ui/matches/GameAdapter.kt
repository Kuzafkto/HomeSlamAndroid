package com.example.tfgproject.ui.matches

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tfgproject.databinding.GameListItemBinding
import com.example.tfgproject.model.Game

class GameAdapter(private var games: List<Game>, private val viewModel: GamesViewModel) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    inner class GameViewHolder(private val binding: GameListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game) {
            binding.textViewGameDate.text = game.gameDate
            binding.textViewLocal.text = viewModel.getTeamName(game.local ?: "")
            binding.textViewVisitor.text = viewModel.getTeamName(game.visitor ?: "")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = GameListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) = holder.bind(games[position])

    override fun getItemCount() = games.size

    fun updateGames(newGames: List<Game>) {
        games = newGames
        notifyDataSetChanged()
    }
}
