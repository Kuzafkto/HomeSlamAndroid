package com.example.tfgproject.ui.matches

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class GamesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _teamDetails = MutableStateFlow<Map<String, TeamDetails>>(mapOf())
    val teamDetails: StateFlow<Map<String, TeamDetails>> = _teamDetails

    init {
        loadGames()
    }

    private fun loadGames() {
        db.collection("games").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("GamesViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }
            val gamesList = mutableListOf<Game>()
            snapshot?.documents?.forEach { document ->
                document.toObject(Game::class.java)?.let { game ->
                    game.id = document.id
                    gamesList.add(game)
                    loadTeamData(game.local)
                    loadTeamData(game.visitor)
                }
            }
            _games.value = gamesList
        }
    }

    private fun loadTeamData(teamId: String?) {
        teamId?.let { id ->
            db.collection("teams").document(id).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("GamesViewModel", "Error loading team data for ID: $id", e)
                    return@addSnapshotListener
                }
                val team = snapshot?.toObject(Team::class.java)
                team?.let {
                    val details = _teamDetails.value.toMutableMap()
                    details[id] = TeamDetails(it.name, it.imageUrl)
                    _teamDetails.value = details
                    updateGamesWithNewTeamDetails(id, it)
                }
            }
        }
    }
    private fun updateGamesWithNewTeamDetails(teamId: String, team: Team) {
        val updatedGames = _games.value.map { game ->
            when (teamId) {
                game.local -> game.copy(localName = team.name, localImage = team.imageUrl)
                game.visitor -> game.copy(visitorName = team.name, visitorImage = team.imageUrl)
                else -> game
            }
        }
        _games.value = updatedGames
    }

}


