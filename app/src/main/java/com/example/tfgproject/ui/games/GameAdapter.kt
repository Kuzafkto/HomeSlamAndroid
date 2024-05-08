package com.example.tfgproject.ui.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.tfgproject.R
import com.example.tfgproject.databinding.GameListItemBinding
import com.example.tfgproject.model.Game

class GameAdapter(
    private var games: List<Game>,
    private val viewModel: GamesViewModel,
    private val onGameClicked: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    inner class GameViewHolder(private val binding: GameListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                games.getOrNull(adapterPosition)?.let(onGameClicked)
            }
        }

        fun bind(game: Game) {
            val localDetails = viewModel.teamDetails.value[game.local]
            val visitorDetails = viewModel.teamDetails.value[game.visitor]

            binding.textViewLocalTeam.text = localDetails?.name ?: "Unknown"
            binding.textViewVisitorTeam.text = visitorDetails?.name ?: "Unknown"
            binding.textViewLocalScore.text = game.localRuns?.toString() ?: "0"
            binding.textViewVisitorScore.text = game.visitorRuns?.toString() ?: "0"

            localDetails?.imageUrl?.let {
                binding.imageViewLocal.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_image)
                    error(R.drawable.error_image)
                    transformations(CircleCropTransformation())
                }
            }

            visitorDetails?.imageUrl?.let {
                binding.imageViewVisitor.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_image)
                    error(R.drawable.error_image)
                    transformations(CircleCropTransformation())
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
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = games.size
            override fun getNewListSize(): Int = newGames.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return games[oldItemPosition].id == newGames[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return games[oldItemPosition] == newGames[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        games = newGames
        diffResult.dispatchUpdatesTo(this)
    }
}
