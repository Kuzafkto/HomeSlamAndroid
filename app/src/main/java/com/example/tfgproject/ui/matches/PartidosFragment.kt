package com.example.tfgproject.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentPartidosBinding // Asegúrate de tener este import correcto
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.launch

class PartidosFragment : Fragment() {

    private lateinit var binding: FragmentPartidosBinding
    private val viewModel: GamesViewModel by viewModels()
    private lateinit var toolbarViewModel: ToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPartidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarViewModel.setTitle("Partidos")
        val gameAdapter = GameAdapter(emptyList(), viewModel)
        binding.recyclerViewGames.adapter = gameAdapter
        binding.recyclerViewGames.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.games.collect { games ->
                gameAdapter.updateGames(games)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.namesLoaded.collect { loaded ->
                if (loaded) {
                    gameAdapter.notifyDataSetChanged()
                }
            }
        }
    }

}

