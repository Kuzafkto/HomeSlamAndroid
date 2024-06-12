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

/**
 * Adapter for displaying a list of games in a RecyclerView.
 *
 * @property games The list of games to display.
 * @property viewModel The ViewModel associated with the games list.
 * @property onGameClicked Callback to be invoked when a game item is clicked.
 */
class GameAdapter(
    private var games: List<Game>,
    private val viewModel: GamesViewModel,
    private val onGameClicked: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    /**
     * ViewHolder for displaying individual game items.
     *
     * @property binding The binding object for the game list item layout.
     */
    inner class GameViewHolder(private val binding: GameListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                games.getOrNull(adapterPosition)?.let(onGameClicked)
            }
        }

        /**
         * Binds the game data to the view holder.
         *
         * @param game The game to bind.
         */
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

    /**
     * Creates a new ViewHolder for a game item.
     *
     * @param parent The parent view group.
     * @param viewType The view type of the new view.
     * @return The newly created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = GameListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    /**
     * Binds the game data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) = holder.bind(games[position])

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = games.size

    /**
     * Updates the list of games and notifies the adapter of the changes.
     *
     * @param newGames The new list of games.
     */
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
