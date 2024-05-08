package com.example.tfgproject.ui.teams

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TeamsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    init {
        loadTeams()
    }

    private fun loadTeams() {
        db.collection("teams").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TeamsViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }

            val teamList = mutableListOf<Team>()
            snapshot?.documents?.mapNotNull { it.toObject(Team::class.java)?.apply { id = it.id } }?.let {
                teamList.addAll(it)
                it.forEach { team ->
                    calculateWinsAndLosses(team)
                }
            }
            _teams.value = teamList
        }
    }

    private fun calculateWinsAndLosses(team: Team) {
        db.collection("games")
            .whereEqualTo("local", team.id)
            .addSnapshotListener { localSnapshot, e ->
                if (e != null) {
                    Log.w("TeamsViewModel", "Listen failed for local games.", e)
                    return@addSnapshotListener
                }
                val localGames = localSnapshot?.documents?.mapNotNull { it.toObject(Game::class.java) }
                val localWins = localGames?.count { it.localRuns!! > it.visitorRuns!! } ?: 0

                db.collection("games")
                    .whereEqualTo("visitor", team.id)
                    .addSnapshotListener { visitorSnapshot, error ->
                        if (error != null) {
                            Log.w("TeamsViewModel", "Listen failed for visitor games.", error)
                            return@addSnapshotListener
                        }
                        val visitorGames = visitorSnapshot?.documents?.mapNotNull { it.toObject(Game::class.java) }
                        val visitorWins = visitorGames?.count { it.visitorRuns!! > it.localRuns!! } ?: 0

                        val wins = localWins + visitorWins
                        val losses = (localGames?.size ?: 0) + (visitorGames?.size ?: 0) - wins

                        val updatedTeam = team.copy(gamesWon = wins, gamesLost = losses)
                        updateTeamInList(updatedTeam)
                    }
            }
    }

    private fun updateTeamInList(updatedTeam: Team) {
        _teams.value = _teams.value.map {
            if (it.id == updatedTeam.id) updatedTeam else it
        }
    }
}
