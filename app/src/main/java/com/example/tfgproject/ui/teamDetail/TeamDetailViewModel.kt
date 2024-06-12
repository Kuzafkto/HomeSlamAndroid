package com.example.tfgproject.ui.teamDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tfgproject.model.Player
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing team details and players in TeamDetailFragment.
 */
class TeamDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _team = MutableStateFlow<Team?>(null)
    val team: StateFlow<Team?> = _team.asStateFlow()

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    val isTextExpanded = MutableStateFlow(false)  // Manage text expanded state here

    /**
     * Toggles the expanded state of the team story text.
     */
    fun toggleTextExpansion() {
        isTextExpanded.value = !isTextExpanded.value
    }

    /**
     * Loads the details of a team given its [teamId].
     *
     * @param teamId The ID of the team to load.
     */
    fun loadTeamDetails(teamId: String) {
        val teamRef = db.collection("teams").document(teamId)
        teamRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("TeamDetailViewModel", "Error loading team details", e)
                return@addSnapshotListener
            }

            val team = snapshot?.toObject(Team::class.java)?.apply { id = snapshot.id }
            _team.value = team
            team?.players?.let { loadPlayers(it) }
        }
    }

    /**
     * Loads the players for the team given a list of player IDs.
     *
     * @param playerIds The list of player IDs to load.
     */
    private fun loadPlayers(playerIds: List<String>) {
        db.collection("players")
            .whereIn(FieldPath.documentId(), playerIds)
            .addSnapshotListener { snapshot, e ->
                Log.d("snapshot", snapshot.toString())
                Log.d("playerIds", playerIds.toString())
                if (e != null) {
                    Log.e("TeamDetailViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val players = snapshot?.toObjects(Player::class.java) ?: emptyList()
                Log.d("players", players.toString())
                _players.value = players
            }
    }
}
