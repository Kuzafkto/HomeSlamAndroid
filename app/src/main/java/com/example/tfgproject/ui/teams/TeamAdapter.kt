package com.example.tfgproject.ui.teams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tfgproject.databinding.TeamListItemBinding
import com.example.tfgproject.model.Team

class TeamAdapter(private var teams: List<Team>) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(private val binding: TeamListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(team: Team) {
            binding.teamName.text = team.name
            // Configura aquí otros elementos, como imágenes si tienes
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = TeamListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount() = teams.size

    fun updateTeams(newTeams: List<Team>) {
        teams = newTeams
        notifyDataSetChanged()  // Notifica al RecyclerView que los datos han cambiado
    }
}
