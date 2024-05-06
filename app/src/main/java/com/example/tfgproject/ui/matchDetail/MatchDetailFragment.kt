package com.example.tfgproject.ui.matchDetail
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentMatchDetailBinding
import com.example.tfgproject.databinding.FragmentTeamDetailBinding
import com.example.tfgproject.model.Game
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.launch

class MatchDetailFragment : Fragment() {
    private val viewModel: MatchDetailViewModel by viewModels()
    private lateinit var binding: FragmentMatchDetailBinding
    private lateinit var toolbarViewModel: ToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val game = it.getParcelable<Game>("game")
            game?.let {
                viewModel.loadGameDetails(it)
            }
            toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root // Return the root of the binding layout
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGameDetails()
        setupCheckboxBehavior()
    }

    private fun setupCheckboxBehavior() {
        binding.checkBoxLocal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxVisitor.isChecked = false
                viewModel.gameDetails.value?.let {

                    it.game.id?.let { it1 -> viewModel.updateVote(it1, it.localTeam?.id ?: "ERROR PASANDO ID") }
                }
            }
        }

        binding.checkBoxVisitor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxLocal.isChecked = false
                viewModel.gameDetails.value?.let {
                    it.game.id?.let { it1 -> viewModel.updateVote(it1, it.visitorTeam?.id ?: "ERROR PASANDO EL ID") }
                }
            }
        }
    }



    private fun observeGameDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.gameDetails.collect { details ->
                details?.let {
                    toolbarViewModel.setTitle("${it.localTeam?.name} vs ${it.visitorTeam?.name}")
                    binding.tvLocalTeamName.text= it.localTeam?.name?:"Indefinido"
                    binding.tvVisitorTeamName.text= it.visitorTeam?.name?:"Indefinido"
                    binding.tvStory.text = it.game.story ?: "No story available"
                    loadImages(it.localTeam?.imageUrl, it.visitorTeam?.imageUrl)
                }
            }
        }
    }


    private fun loadImages(localImageUrl: String?, visitorImageUrl: String?) {
        localImageUrl?.let {
            binding.imgTeamLocal.load(it) {
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.error_image)
            }
        }
        visitorImageUrl?.let {
            binding.imgTeamVisitor.load(it) {
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.error_image)
            }
        }
    }


}