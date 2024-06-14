package com.example.tfgproject.ui.gameDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for managing game details and user voting in a match.
 *
 * @constructor Creates an instance of [MatchDetailViewModel].
 */
class MatchDetailViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _gameDetails = MutableStateFlow<GameDetails?>(null)

    /**
     * StateFlow to expose the game details.
     */
    val gameDetails: StateFlow<GameDetails?> = _gameDetails

    private val auth = FirebaseAuth.getInstance()
    private val _userVoteTeamId = MutableStateFlow<String?>(null)

    /**
     * StateFlow to expose the user's voted team ID.
     */
    val userVoteTeamId: StateFlow<String?> = _userVoteTeamId

    /**
     * StateFlow to track whether the text is expanded or not.
     */
    val isTextExpanded = MutableStateFlow(false)

    /**
     * Toggles the expansion state of the text.
     */
    fun toggleTextExpansion() {
        isTextExpanded.value = !isTextExpanded.value
    }

    /**
     * StateFlow to determine if the user can vote.
     *
     * @return true if the user can vote, false otherwise.
     */
    val canVote: StateFlow<Boolean> = _gameDetails.map { details ->
        val localRuns = details?.game?.localRuns
        val visitorRuns = details?.game?.visitorRuns
        localRuns == null || visitorRuns == null || localRuns == "?" || visitorRuns == "?"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    /**
     * Creates a new vote for the actual user or updates an already existing one for a game with [gameId] voting for a team with [teamId]
     *
     * @param gameId game's uuid
     * @param teamId team's uuid that user is voting
     *
     */
    fun voteForTeam(gameId: String, teamId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                val userVotes = userDocument["votes"] as? List<String> ?: listOf()

                if (userVotes.isEmpty()) {
                    createVote(gameId, teamId, userVotes, userDocument.reference)
                } else {
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
            .addOnFailureListener { e ->
                Log.e("VoteForTeam", "Error al cargar los votos del usuario", e)
            }
    }

    /**
     * Creates a new vote document in the database.
     *
     * @param gameId The ID of the game being voted on.
     * @param teamId The ID of the team being voted for.
     * @param userVotes The list of vote IDs associated with the user.
     * @param userRef A reference to the user's document in Firestore.
     */
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
                Log.d("MatchDetailViewModel", "voto creado con id : $teamId")
            }
            .addOnFailureListener { e ->
                Log.e("MatchDetailViewModel", "fallo en crear el voto", e)
            }
    }

    /**
     * Updates an existing vote document in the database.
     *
     * @param voteId The ID of the vote document to be updated.
     * @param teamId The ID of the team being voted for.
     */
    private fun updateVote(voteId: String, teamId: String) {
        db.collection("votes").document(voteId)
            .update("reference", teamId)  // Actualizar referencia del equipo
            .addOnSuccessListener {
                Log.d("MatchDetailViewModel", "voto actualizado con id : $teamId")
            }
    }

    /**
     * Loads the details of a game from the database.
     *
     * @param game The game object containing the game ID.
     */
    fun loadGameDetails(game: Game) {
        db.collection("games").document(game.id ?: return)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val updatedGame = it.toObject(Game::class.java)
                    updatedGame?.id = it.id // Asigna manualmente el ID del documento
                    updatedGame?.let { gameWithId ->
                        updateGameDetails(gameWithId)
                    }
                }
            }

        db.collection("teams").document(game.local ?: "")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
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
                    return@addSnapshotListener
                }
                snapshot?.toObject(Team::class.java)?.apply {
                    id = snapshot.id
                    val currentDetails = _gameDetails.value
                    _gameDetails.value = currentDetails?.copy(visitorTeam = this) ?: GameDetails(game, null, this)
                }
            }
    }

    /**
     * Updates the game details with new data.
     *
     * @param game The updated game object.
     */
    private fun updateGameDetails(game: Game) {
        val currentDetails = _gameDetails.value
        _gameDetails.value = GameDetails(game, currentDetails?.localTeam, currentDetails?.visitorTeam)
    }

    /**
     * Loads the user's vote for a specific game from the database.
     *
     * @param gameId The ID of the game for which to load the user's vote.
     */
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
