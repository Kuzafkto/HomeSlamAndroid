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

class TeamAdapter(
    private var teams: List<Team>,
    private val onTeamClicked: (String) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(val binding: TeamListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(team: Team, onTeamClicked: (String) -> Unit) {
            binding.teamName.text = team.name
            binding.teamImage.load(team.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.error_image)
                transformations(CircleCropTransformation())
            }
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = TeamListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position], onTeamClicked)
    }

    override fun getItemCount() = teams.size

    fun updateTeams(newTeams: List<Team>) {
        teams = newTeams
        notifyDataSetChanged()
    }
}
