package com.example.tfgproject.ui.teams

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgproject.model.Team
import com.google.firebase.firestore.FirebaseFirestore

class TeamsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams

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
                doc.toObject(Team::class.java)?.let {
                    teamList.add(it)
                }
            }
            _teams.value = teamList
        }
    }
}

