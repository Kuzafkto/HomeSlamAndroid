package com.example.tfgproject.ui.matches

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.tfgproject.R
import com.example.tfgproject.databinding.GameListItemBinding
import com.example.tfgproject.model.Game

class GameAdapter(private var games: List<Game>, private val viewModel: GamesViewModel) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    inner class GameViewHolder(private val binding: GameListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game) {
            binding.textViewLocalTeam.text = viewModel.getTeamName(game.local ?: "")
            binding.textViewVisitorTeam.text = viewModel.getTeamName(game.visitor ?: "")
            binding.textViewLocalScore.text = game.localRuns.toString()
            binding.textViewVisitorScore.text = game.visitorRuns.toString()

            game.local?.let {
                viewModel.getTeamImageUrl(it)?.let { imageUrl ->
                    binding.imageViewLocal.load(imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.placeholder_image)  // imagen de placeholder
                        error(R.drawable.error_image)  // imagen de error
                        transformations(CircleCropTransformation())
                    }
                    Unit
                }
            }

            game.visitor?.let {
                viewModel.getTeamImageUrl(it)?.let { imageUrl ->
                    binding.imageViewVisitor.load(imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.placeholder_image)  // imagen de placeholder
                        error(R.drawable.error_image)  // imagen de error
                        transformations(CircleCropTransformation())
                    }
                    Unit
                }
            }
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
