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
import com.example.tfgproject.model.Game
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.flow.collectLatest
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGameDetails()
        setupCheckboxBehavior()
        setupReadMoreButton()
    }

    private fun setupReadMoreButton() {
        binding.btnReadMore.setOnClickListener {
            viewModel.toggleTextExpansion()
            updateStoryText()
        }
    }
    private fun updateStoryText() {
        val fullText = viewModel.gameDetails.value?.game?.story ?: getString(R.string.default_story_text)
        val isExpanded = viewModel.isTextExpanded.value
        binding.tvStory.text = if (isExpanded) {
            fullText
        } else {
            if (fullText.length > 200) fullText.take(200) + "..." else fullText
        }
        binding.btnReadMore.text = if (isExpanded) getString(R.string.read_less) else getString(R.string.read_more)
    }


    private fun setupCheckboxBehavior() {
        binding.checkBoxLocal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxVisitor.isChecked = false
                viewModel.gameDetails.value?.let { details ->
                    details.localTeam?.id?.let { teamId ->
                        details.game.id?.let { gameId ->
                            viewModel.voteForTeam(gameId, teamId)
                        }
                    }
                }
            }
        }

        binding.checkBoxVisitor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxLocal.isChecked = false
                viewModel.gameDetails.value?.let { details ->
                    details.visitorTeam?.id?.let { teamId ->
                        details.game.id?.let { gameId ->
                            viewModel.voteForTeam(gameId, teamId)
                        }
                    }
                }
            }
        }
    }


    private fun observeGameDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.gameDetails.collectLatest { details ->
                details?.let {
                    viewModel.loadUserVote(it.game.id ?: "")
                    toolbarViewModel.setTitle("${it.localTeam?.name} vs ${it.visitorTeam?.name}")
                    binding.tvLocalTeamName.text = it.localTeam?.name ?: "Indefinido"
                    binding.tvVisitorTeamName.text = it.visitorTeam?.name ?: "Indefinido"
                    updateStoryText()
                    //binding.tvStory.text = it.game.story ?: "No story available"
                    loadImages(it.localTeam?.imageUrl, it.visitorTeam?.imageUrl)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userVoteTeamId.collect { teamId ->
                updateCheckboxes(teamId)
            }
        }
    }

    private fun updateCheckboxes(teamId: String?) {
        val localTeamId = viewModel.gameDetails.value?.localTeam?.id
        val visitorTeamId = viewModel.gameDetails.value?.visitorTeam?.id

        if (localTeamId != null && visitorTeamId != null) {
            binding.checkBoxLocal.isChecked = teamId == localTeamId
            binding.checkBoxVisitor.isChecked = teamId == visitorTeamId
        } else {
            // esto es para q no las marque como true al iniciar el fragmento pq aun no estan seteados
            binding.checkBoxLocal.isChecked = false
            binding.checkBoxVisitor.isChecked = false
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