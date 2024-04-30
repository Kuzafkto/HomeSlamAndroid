package com.example.tfgproject.ui.teams
import com.google.firebase.firestore.FirebaseFirestore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentEquiposBinding
import com.example.tfgproject.model.Team
import com.example.tfgproject.ui.toolbar.ToolbarViewModel


class EquiposFragment : Fragment() {
    private lateinit var binding: FragmentEquiposBinding
    private val viewModel: TeamsViewModel by viewModels()
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEquiposBinding.inflate(inflater, container, false)
        teamAdapter = TeamAdapter(emptyList())
        binding.recyclerViewTeams.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(context)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.teams.observe(viewLifecycleOwner) { teams ->
            teamAdapter.updateTeams(teams)
        }
    }
}



