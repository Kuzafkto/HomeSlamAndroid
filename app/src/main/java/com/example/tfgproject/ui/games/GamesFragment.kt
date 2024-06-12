package com.example.tfgproject.ui.games

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentGamesBinding
import com.example.tfgproject.model.Game
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.launch

/**
 * A Fragment that displays a list of games.
 * It uses a RecyclerView to display the list and interacts with a ViewModel to get the data.
 */
class GamesFragment : Fragment() {

    private lateinit var binding: FragmentGamesBinding
    private val viewModel: GamesViewModel by viewModels()
    private lateinit var toolbarViewModel: ToolbarViewModel

    /**
     * Called when the fragment is created.
     * Initializes the ToolbarViewModel.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarViewModel.setTitle(getString(R.string.title_partidos))
        val gameAdapter = GameAdapter(emptyList(), viewModel) { game ->
            Log.d("GAME", game.toString())
            val action = GamesFragmentDirections.actionPartidosFragmentToMatchDetailFragment(game as Game)
            findNavController().navigate(action)
        }
        binding.recyclerViewGames.adapter = gameAdapter
        binding.recyclerViewGames.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.games.collect { games ->
                gameAdapter.updateGames(games)
            }
        }
    }
}
