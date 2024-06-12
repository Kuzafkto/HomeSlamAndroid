package com.example.tfgproject.ui.teams

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.tfgproject.R
import com.example.tfgproject.databinding.TeamListItemBinding
import com.example.tfgproject.model.Team

/**
 * Adapter for displaying a list of teams in a RecyclerView.
 *
 * @property teams The list of teams to display.
 * @property onTeamClicked The callback to invoke when a team is clicked, with the team ID.
 */
class TeamAdapter(
    private var teams: List<Team>,
    private val onTeamClicked: (String) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    /**
     * ViewHolder for the team items.
     *
     * @property binding The binding object for the team list item layout.
     */
    class TeamViewHolder(val binding: TeamListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds the team data to the views.
         *
         * @param team The team data.
         * @param onTeamClicked The callback to invoke when the team item is clicked.
         */
        fun bind(team: Team, onTeamClicked: (String) -> Unit) {
            binding.teamName.text = team.name
            binding.teamImage.load(team.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.error_image)
                transformations(CircleCropTransformation())
            }
            binding.textGamesWon.text = team.gamesWon.toString()
            binding.textGamesLost.text = team.gamesLost.toString()
            binding.root.setOnClickListener {
                if (team.id.isNullOrEmpty()) {
                    Log.e("TeamAdapter", "Error: Team ID is null or empty for team: ${team.name}")
                } else {
                    Log.d("TeamAdapter", "Navigating with team ID: ${team.id}")
                    onTeamClicked(team.id!!)
                }
            }
        }
    }

    /**
     * Inflates the view holder for the team items.
     *
     * @param parent The parent view group.
     * @param viewType The view type of the new view.
     * @return The inflated view holder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = TeamListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    /**
     * Binds the team data to the view holder.
     *
     * @param holder The view holder.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position], onTeamClicked)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = teams.size

    /**
     * Updates the list of teams and notifies the adapter of the data change.
     *
     * @param newTeams The new list of teams.
     */
    fun updateTeams(newTeams: List<Team>) {
        teams = newTeams
        notifyDataSetChanged()
    }
}
