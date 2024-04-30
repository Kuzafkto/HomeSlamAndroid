package com.example.tfgproject.ui.matches

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GamesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    private val teamNames = mutableMapOf<String, String>()
    private val _namesLoaded = MutableLiveData<Boolean>(false)
    val namesLoaded: LiveData<Boolean> = _namesLoaded
    private var teamListenerRegistration: ListenerRegistration? = null

    init {
        loadTeamNames()
        loadGames()
    }

    private fun loadTeamNames() {
        teamListenerRegistration = db.collection("teams")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("GamesViewModel", "Listen for team names failed.", e)
                    _namesLoaded.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        val team = document.toObject(Team::class.java)
                        team?.name?.let { name ->
                            teamNames[document.id] = name
                        }
                    }
                    _namesLoaded.value = true
                } else {
                    Log.d("GamesViewModel", "Current data: null")
                    _namesLoaded.value = false
                }
            }
    }


    fun getTeamName(teamId: String): String = teamNames[teamId] ?: "Unknown Team ID"

    private fun loadGames() {
        db.collection("games").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("GamesViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }
            val gameList = snapshot?.toObjects(Game::class.java)
            _games.value = gameList ?: emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        teamListenerRegistration?.remove()  // Remover listener cuando el ViewModel se destruya
    }
}

