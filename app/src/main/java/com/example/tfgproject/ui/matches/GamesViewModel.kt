package com.example.tfgproject.ui.matches

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FirebaseFirestore

class GamesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    private val teamNames = mutableMapOf<String, String>()
    private val _namesLoaded = MutableLiveData<Boolean>(false)
    val namesLoaded: LiveData<Boolean> = _namesLoaded

    init {
        loadTeamNames()
        loadGames()
    }

    private fun loadTeamNames() {
        db.collection("teams").get().addOnSuccessListener { result ->
            for (document in result.documents) {
                val team = document.toObject(Team::class.java)
                team?.name?.let { name ->
                    teamNames[document.id] = name
                }
            }
            _namesLoaded.value = true  // Indicar que la carga de nombres ha finalizado
        }.addOnFailureListener {
            _namesLoaded.value = false  // Indicar fallo en la carga
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
}

