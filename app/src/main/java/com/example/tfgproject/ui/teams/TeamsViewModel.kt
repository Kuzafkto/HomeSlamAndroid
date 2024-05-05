package com.example.tfgproject.ui.teams

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TeamsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    init {
        loadTeams()
    }

    private fun loadTeams() {
        db.collection("teams").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TeamsViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }

            val teamList = mutableListOf<Team>()
            for (doc in snapshot!!) {
                doc.toObject(Team::class.java).let {
                    it.id = doc.id  //id del documento al objeto team
                    teamList.add(it)
                }
            }
            _teams.value = teamList
        }
    }

}

