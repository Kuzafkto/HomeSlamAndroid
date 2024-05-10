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
            .addSnapshotListener { localSnapshot, localError ->
                if (localError != null) {
                    Log.w("TeamsViewModel", "Listen failed for local games.", localError)
                    return@addSnapshotListener
                }
                val localWins = localSnapshot?.documents?.count {
                    val game = it.toObject(Game::class.java)
                    game != null && isValidGame(game) && game.localRuns!!.toInt() > game.visitorRuns!!.toInt()
                } ?: 0

                val localLosses = localSnapshot?.documents?.count {
                    val game = it.toObject(Game::class.java)
                    game != null && isValidGame(game) && game.localRuns!!.toInt() < game.visitorRuns!!.toInt()
                } ?: 0

                db.collection("games")
                    .whereEqualTo("visitor", team.id)
                    .addSnapshotListener { visitorSnapshot, visitorError ->
                        if (visitorError != null) {
                            Log.w("TeamsViewModel", "Listen failed for visitor games.", visitorError)
                            return@addSnapshotListener
                        }
                        val visitorWins = visitorSnapshot?.documents?.count {
                            val game = it.toObject(Game::class.java)
                            game != null && isValidGame(game) && game.visitorRuns!!.toInt() > game.localRuns!!.toInt()
                        } ?: 0

                        val visitorLosses = visitorSnapshot?.documents?.count {
                            val game = it.toObject(Game::class.java)
                            game != null && isValidGame(game) && game.visitorRuns!!.toInt() < game.localRuns!!.toInt()
                        } ?: 0

                        val totalWins = localWins + visitorWins
                        val totalLosses = localLosses + visitorLosses

                        val updatedTeam = team.copy(gamesWon = totalWins, gamesLost = totalLosses)
                        updateTeamInList(updatedTeam)
                    }
            }
    }


    private fun isValidGame(game: Game?): Boolean {
        return game?.localRuns?.toIntOrNull() != null && game.visitorRuns?.toIntOrNull() != null
    }





    private fun updateTeamInList(updatedTeam: Team) {
        _teams.value = _teams.value.map {
            if (it.id == updatedTeam.id) updatedTeam else it
        }
    }
}
