package com.example.tfgproject.ui.teamDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentTeamDetailBinding
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TeamDetailFragment : Fragment() {
    private lateinit var binding: FragmentTeamDetailBinding
    private val viewModel: TeamDetailViewModel by viewModels()
    private lateinit var toolbarViewModel: ToolbarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val teamId = TeamDetailFragmentArgs.fromBundle(requireArguments()).teamId
        viewModel.loadTeamDetails(teamId)
        setupRecyclerView()
        observeTeamAndPlayers()
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewPlayers.layoutManager = gridLayoutManager
        binding.recyclerViewPlayers.adapter = PlayerAdapter(emptyList())
    }


    private fun observeTeamAndPlayers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.team.collect { team ->
                    if (team != null) {
                        // Actualiza el texto de la historia
                        binding.textViewStory.text = team.story ?: getString(R.string.default_story_text)

                        // Carga la imagen del equipo
                        binding.imageViewTeam.load(team.imageUrl) {
                            placeholder(R.drawable.placeholder_image)
                            error(R.drawable.error_image)
                        }
                        toolbarViewModel.setTitle(team.name ?: getString(R.string.default_title))
                    }
                }
            }

            launch {
                viewModel.players.collect { players ->
                    (binding.recyclerViewPlayers.adapter as PlayerAdapter).updatePlayers(players)
                }
            }
        }
    }


}

