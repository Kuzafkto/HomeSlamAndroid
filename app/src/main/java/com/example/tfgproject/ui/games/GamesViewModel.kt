package com.example.tfgproject.ui.games

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing game data and team details.
 */
class GamesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Backing property for the list of games
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    // Backing property for the team details map
    private val _teamDetails = MutableStateFlow<Map<String, TeamDetails>>(mapOf())
    val teamDetails: StateFlow<Map<String, TeamDetails>> = _teamDetails

    /**
     * Initializes the ViewModel by loading the games.
     */
    init {
        loadGames()
    }

    /**
     * Loads the list of games from Firestore and listens for changes.
     * Updates the list of games and team details.
     */
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

    /**
     * Loads the team data for a given team ID from Firestore and listens for changes.
     * Updates the team details map and the list of games with the new team details.
     *
     * @param teamId The ID of the team to load data for.
     */
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

    /**
     * Updates the list of games with new team details.
     *
     * @param teamId The ID of the team whose details were updated.
     * @param team The updated team details.
     */
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
