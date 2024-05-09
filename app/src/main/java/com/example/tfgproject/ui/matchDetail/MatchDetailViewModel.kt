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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MatchDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _gameDetails = MutableStateFlow<GameDetails?>(null)
    val gameDetails: StateFlow<GameDetails?> = _gameDetails
    private val userId = FirebaseAuth.getInstance().currentUser?.uid  // Asumiendo que el usuario está autenticado
    private val auth = FirebaseAuth.getInstance()
    private val _userVoteTeamId = MutableStateFlow<String?>(null)
    val userVoteTeamId: StateFlow<String?> = _userVoteTeamId
    val isTextExpanded = MutableStateFlow(false)  // Manage text expanded state here

    fun toggleTextExpansion() {
        isTextExpanded.value = !isTextExpanded.value
    }


    fun voteForTeam(gameId: String, teamId: String) {
        val userId = auth.currentUser?.uid ?: return  // Salir si no hay usuario autenticado

        // Obtener los votos del usuario
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
                // Agregar la nueva referencia de voto al array de votos del usuario
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
        viewModelScope.launch {
            val localRef = db.collection("teams").document(game.local ?: "")
            localRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MatchDetailViewModel", "Error loading local team data", e)
                    return@addSnapshotListener
                }
                val localTeam = snapshot?.toObject(Team::class.java)?.apply { id = snapshot.id }
                val visitorTeam = _gameDetails.value?.visitorTeam
                _gameDetails.value = GameDetails(game, localTeam, visitorTeam)
            }

            val visitorRef = db.collection("teams").document(game.visitor ?: "")
            visitorRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MatchDetailViewModel", "Error loading visitor team data", e)
                    return@addSnapshotListener
                }
                val visitorTeam = snapshot?.toObject(Team::class.java)?.apply { id = snapshot.id }
                val localTeam = _gameDetails.value?.localTeam
                _gameDetails.value = GameDetails(game, localTeam, visitorTeam)
            }
        }
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
