package com.example.tfgproject.ui.matchDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MatchDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _gameDetails = MutableStateFlow<GameDetails?>(null)
    val gameDetails: StateFlow<GameDetails?> = _gameDetails
    private val auth = FirebaseAuth.getInstance()
    private val _userVoteTeamId = MutableStateFlow<String?>(null)
    val userVoteTeamId: StateFlow<String?> = _userVoteTeamId
    val isTextExpanded = MutableStateFlow(false)

    fun toggleTextExpansion() {
        isTextExpanded.value = !isTextExpanded.value
    }


    fun voteForTeam(gameId: String, teamId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                val userVotes = userDocument["votes"] as? List<String> ?: listOf()
                if (userVotes.isEmpty()) {
                    // No hay votos, crear uno nuevo directamente
                    createVote(gameId, teamId, userVotes, userDocument.reference)
                } else {
                    // Buscar si ya existe un voto para este juego
                    db.collection("votes").whereIn(FieldPath.documentId(), userVotes).get()
                        .addOnSuccessListener { votesSnapshot ->
                            val existingVote = votesSnapshot.documents.find { it.getString("game") == gameId }
                            if (existingVote != null) {
                                updateVote(existingVote.id, teamId)
                            } else {
                                createVote(gameId, teamId, userVotes, userDocument.reference)
                            }
                        }
                }
            }
    }

    private fun createVote(gameId: String, teamId: String, userVotes: List<String>, userRef: DocumentReference) {
        val newVoteData = mapOf(
            "game" to gameId,
            "reference" to teamId,
            "category" to "winnerTeam"
        )
        db.collection("votes").add(newVoteData)
            .addOnSuccessListener { voteRef ->
                val updatedVotes = userVotes + voteRef.id
                userRef.update("votes", updatedVotes)
                Log.d("MatchDetailViewModel", "Vote successfully created for team ID: $teamId")
            }
            .addOnFailureListener { e ->
                Log.e("MatchDetailViewModel", "Failed to create vote", e)
            }
    }


    private fun updateVote(voteId: String, teamId: String) {
        db.collection("votes").document(voteId)
            .update("reference", teamId)  // Actualizar referencia del equipo
            .addOnSuccessListener {
                Log.d("MatchDetailViewModel", "Vote updated successfully for team ID: $teamId")
            }
    }


    fun loadGameDetails(game: Game) {
        db.collection("games").document(game.id ?: return)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MatchDetailViewModel", "Error loading game data", e)
                    return@addSnapshotListener
                }
                snapshot?.toObject(Game::class.java)?.let { updatedGame ->
                    updateGameDetails(updatedGame)
                }
            }

        db.collection("teams").document(game.local ?: "")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MatchDetailViewModel", "Error loading local team data", e)
                    return@addSnapshotListener
                }
                snapshot?.toObject(Team::class.java)?.apply {
                    id = snapshot.id
                    val currentDetails = _gameDetails.value
                    _gameDetails.value = currentDetails?.copy(localTeam = this) ?: GameDetails(game, this, null)
                }
            }

        db.collection("teams").document(game.visitor ?: "")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MatchDetailViewModel", "Error loading visitor team data", e)
                    return@addSnapshotListener
                }
                snapshot?.toObject(Team::class.java)?.apply {
                    id = snapshot.id
                    val currentDetails = _gameDetails.value
                    _gameDetails.value = currentDetails?.copy(visitorTeam = this) ?: GameDetails(game, null, this)
                }
            }
    }

    private fun updateGameDetails(game: Game) {
        val currentDetails = _gameDetails.value
        _gameDetails.value = GameDetails(game, currentDetails?.localTeam, currentDetails?.visitorTeam)
    }
    fun loadUserVote(gameId: String) {
        val userId = auth.currentUser?.uid ?: return  // Asegurar que el usuario está autenticado

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                val votesIds = userDocument["votes"] as? List<String> ?: listOf()

                if (votesIds.isEmpty()) {
                    _userVoteTeamId.value = null
                } else {
                    db.collection("votes")
                        .whereIn(FieldPath.documentId(), votesIds)
                        .whereEqualTo("game", gameId)
                        .get()
                        .addOnSuccessListener { voteDocuments ->
                            if (voteDocuments.isEmpty) {
                                _userVoteTeamId.value = null
                            } else {
                                val teamId = voteDocuments.documents.first().getString("reference")
                                _userVoteTeamId.value = teamId
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MatchDetailViewModel", "error cargando los votos", e)
                _userVoteTeamId.value = null
            }
    }



}
