package com.example.tfgproject.ui.matchDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MatchDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _gameDetails = MutableStateFlow<GameDetails?>(null)
    val gameDetails: StateFlow<GameDetails?> = _gameDetails

    fun loadGameDetails(game: Game) {
        viewModelScope.launch {
            val localRef = db.collection("teams").document(game.local ?: "")
            localRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MatchDetailViewModel", "Error loading local team data", e)
                    return@addSnapshotListener
                }
                val localTeam = snapshot?.toObject(Team::class.java)
                val visitorTeam = _gameDetails.value?.visitorTeam
                _gameDetails.value = GameDetails(game, localTeam, visitorTeam)
            }

            val visitorRef = db.collection("teams").document(game.visitor ?: "")
            visitorRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MatchDetailViewModel", "Error loading visitor team data", e)
                    return@addSnapshotListener
                }
                val visitorTeam = snapshot?.toObject(Team::class.java)
                val localTeam = _gameDetails.value?.localTeam
                _gameDetails.value = GameDetails(game, localTeam, visitorTeam)
            }
        }
    }

    // Agregar un voto al equipo seleccionado para un partido específico
    fun updateVote(gameId: String, teamId: String) {
        val vote = hashMapOf(
            "category" to "winnerTeam",
            "game" to gameId,
            "reference" to teamId
        )

        db.collection("votes").add(vote).addOnSuccessListener {
            Log.d("MatchDetailViewModel", "Vote added successfully for team ID: $teamId")
        }.addOnFailureListener {
            Log.e("MatchDetailViewModel", "Error adding vote", it)
        }
    }
}
