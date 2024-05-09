package com.example.tfgproject.ui.teams
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentEquiposBinding
import com.example.tfgproject.model.Team
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EquiposFragment : Fragment() {
    private var _binding: FragmentEquiposBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeamsViewModel by viewModels()
    private lateinit var toolbarViewModel: ToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEquiposBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarViewModel.setTitle(getString(R.string.title_equipos))

        val teamAdapter = TeamAdapter(emptyList()) { teamId ->
            val action = EquiposFragmentDirections.actionEquiposFragmentToTeamDetailFragment(teamId)
            findNavController().navigate(action)
        }

        binding.recyclerViewTeams.apply {
            adapter = teamAdapter
            // Establece el layoutManager a GridLayoutManager con 2 columnas
            layoutManager = GridLayoutManager(context, 2)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.teams.collect { teams ->
                teamAdapter.updateTeams(teams)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}