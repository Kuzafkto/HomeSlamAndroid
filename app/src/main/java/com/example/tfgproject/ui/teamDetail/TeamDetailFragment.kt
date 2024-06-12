package com.example.tfgproject.ui.teamDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentTeamDetailBinding
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Fragment for displaying the details of a team.
 */
class TeamDetailFragment : Fragment() {
    private lateinit var binding: FragmentTeamDetailBinding
    private val viewModel: TeamDetailViewModel by viewModels()
    private lateinit var toolbarViewModel: ToolbarViewModel

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
        return binding.root
    }

    /**
     * Called immediately after onCreateView().
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val teamId = TeamDetailFragmentArgs.fromBundle(requireArguments()).teamId
        viewModel.loadTeamDetails(teamId)
        setupRecyclerView()
        observeTeamAndPlayers()
        setupReadMoreButton()
    }

    /**
     * Sets up the RecyclerView for displaying player information.
     */
    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewPlayers.layoutManager = gridLayoutManager
        binding.recyclerViewPlayers.adapter = PlayerAdapter(emptyList())
    }

    /**
     * Observes changes in team and player data, updating the UI accordingly.
     */
    private fun observeTeamAndPlayers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.team.collect { team ->
                if (team != null) {
                    toolbarViewModel.setTitle(team.name ?: getString(R.string.title_equipos))
                    binding.imageViewTeam.load(team.imageUrl) {
                        placeholder(R.drawable.placeholder_image)
                        error(R.drawable.error_image)
                    }
                    updateStoryText()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.players.collect { players ->
                (binding.recyclerViewPlayers.adapter as PlayerAdapter).updatePlayers(players)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isTextExpanded.collect {
                updateStoryText()
            }
        }
    }

    /**
     * Sets up the button for toggling the story text expansion.
     */
    private fun setupReadMoreButton() {
        binding.btnReadMore.setOnClickListener {
            viewModel.toggleTextExpansion()  // Toggle the expanded state in ViewModel
            updateStoryText()
        }
    }

    /**
     * Updates the story text view based on the expanded state.
     */
    private fun updateStoryText() {
        val fullText = viewModel.team.value?.story ?: getString(R.string.default_story_text)
        val isExpanded = viewModel.isTextExpanded.value
        Log.d("valuetest", viewModel.isTextExpanded.value.toString())
        binding.tvStory.text = if (isExpanded) {
            fullText
        } else {
            if (fullText.length > 200) fullText.take(200) + "..." else fullText
        }
        binding.btnReadMore.text = if (isExpanded) getString(R.string.read_less) else getString(R.string.read_more)
    }
}
