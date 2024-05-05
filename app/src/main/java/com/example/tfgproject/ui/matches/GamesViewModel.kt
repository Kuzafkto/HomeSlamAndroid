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

    private val teamNames = mutableMapOf<String, Team>()
    private val _namesLoaded = MutableStateFlow(false)
    val namesLoaded: StateFlow<Boolean> = _namesLoaded.asStateFlow()

    init {
        loadTeamNames()
        loadGames()
    }

    private fun loadTeamNames() {
        db.collection("teams")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("GamesViewModel", "Listen for team names failed.", e)
                    _namesLoaded.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    teamNames.clear()
                    for (document in snapshot.documents) {
                        val team = document.toObject(Team::class.java)
                        team?.let { teamNames[document.id] = it }
                    }
                    _namesLoaded.value = true
                } else {
                    Log.d("GamesViewModel", "Current data: null")
                    _namesLoaded.value = false
                }
            }
    }

    fun getTeamName(teamId: String): String = teamNames[teamId]?.name ?: "Unknown Team ID"

    fun getTeamImageUrl(teamId: String): String? = teamNames[teamId]?.imageUrl

    private fun loadGames() {
        db.collection("games").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("GamesViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }
            _games.value = snapshot?.toObjects(Game::class.java) ?: emptyList()
        }
    }
}