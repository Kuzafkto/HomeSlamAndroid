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

class TeamDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _team = MutableStateFlow<Team?>(null)
    val team: StateFlow<Team?> = _team.asStateFlow()

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    fun loadTeamDetails(teamId: String) {
        db.collection("teams").document(teamId).get().addOnSuccessListener { document ->
            val team = document.toObject(Team::class.java)?.apply { id = document.id }
            _team.value = team
            team?.players?.let { loadPlayers(it) }
        }.addOnFailureListener { e ->
            Log.e("TeamDetailViewModel", "Error loading team details", e)
        }
    }

    private fun loadPlayers(playerIds: List<String>) {
        db.collection("players")
            .whereIn(FieldPath.documentId(), playerIds)
            .addSnapshotListener { snapshot, e ->
                Log.d("snapshot",snapshot.toString())

                Log.d("playerIds",playerIds.toString())
                if (e != null) {
                    Log.e("TeamDetailViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val players = snapshot?.toObjects(Player::class.java) ?: emptyList()
                Log.d("players",players.toString())

                _players.value = players
            }
    }
}
