package com.example.tfgproject.ui.gameDetail

import android.os.Bundle
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

/**
 * A Fragment for displaying the details of a specific match.
 */
class MatchDetailFragment : Fragment() {

    private val viewModel: MatchDetailViewModel by viewModels()
    private lateinit var binding: FragmentMatchDetailBinding
    private lateinit var toolbarViewModel: ToolbarViewModel

    /**
     * Called when the fragment is being created.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
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

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGameDetails()
        observeCanVote()
        setupCheckboxBehavior()
        setupReadMoreButton()
    }

    /**
     * Sets up the "Read More" button behavior to expand or collapse the story text.
     */
    private fun setupReadMoreButton() {
        binding.btnReadMore.setOnClickListener {
            viewModel.toggleTextExpansion()
            updateStoryText()
        }
    }

    /**
     * Updates the story text view based on whether it is expanded or collapsed.
     */
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

    /**
     * Sets up the behavior of the checkboxes for voting on teams.
     */
    private fun setupCheckboxBehavior() {
        binding.checkBoxLocal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxVisitor.isChecked = false
                val details = viewModel.gameDetails.value
                details?.let {
                    viewModel.voteForTeam(it.game.id ?: "", it.localTeam?.id ?: "")
                }
            }
        }

        binding.checkBoxVisitor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxLocal.isChecked = false
                val details = viewModel.gameDetails.value
                details?.let {
                    viewModel.voteForTeam(it.game.id ?: "", it.visitorTeam?.id ?: "")
                }
            }
        }
    }

    /**
     * Observes changes in the game details and updates the UI accordingly.
     */
    private fun observeGameDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.gameDetails.collectLatest { details ->
                details?.let {
                    toolbarViewModel.setTitle("${it.localTeam?.name} vs ${it.visitorTeam?.name}")
                    binding.tvLocalTeamName.text = it.localTeam?.name ?: "Indefinido"
                    binding.tvVisitorTeamName.text = it.visitorTeam?.name ?: "Indefinido"
                    updateStoryText()
                    loadImages(it.localTeam?.imageUrl, it.visitorTeam?.imageUrl)
                    viewModel.loadUserVote(it.game.id ?: "")
                    updateCheckboxVisibility(viewModel.canVote.value)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userVoteTeamId.collectLatest { teamId ->
                updateCheckboxes(teamId)
            }
        }
    }

    /**
     * Observes changes in the voting eligibility and updates the UI accordingly.
     */
    private fun observeCanVote() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.canVote.collectLatest { canVote ->
                binding.checkBoxLocal.isEnabled = canVote
                binding.checkBoxVisitor.isEnabled = canVote
                updateCheckboxVisibility(canVote)
            }
        }
    }

    /**
     * Updates the visibility of the checkboxes based on the voting eligibility.
     *
     * @param canVote Whether the user can vote or not.
     */
    private fun updateCheckboxVisibility(canVote: Boolean) {
        if (canVote) {
            binding.checkBoxLocal.visibility = View.VISIBLE
            binding.checkBoxVisitor.visibility = View.VISIBLE
        } else {
            binding.checkBoxLocal.visibility = View.GONE
            binding.checkBoxVisitor.visibility = View.GONE
        }
    }

    /**
     * Updates the state of the checkboxes based on the user's vote.
     *
     * @param teamId The ID of the team that the user has voted for.
     */
    private fun updateCheckboxes(teamId: String?) {
        val localTeamId = viewModel.gameDetails.value?.localTeam?.id
        val visitorTeamId = viewModel.gameDetails.value?.visitorTeam?.id

        if (localTeamId != null && visitorTeamId != null) {
            binding.checkBoxLocal.isChecked = teamId == localTeamId
            binding.checkBoxVisitor.isChecked = teamId == visitorTeamId
        } else {
            // Esto es para que no las marque como true al iniciar el fragmento porque aún no están seteados
            binding.checkBoxLocal.isChecked = false
            binding.checkBoxVisitor.isChecked = false
        }
    }

    /**
     * Loads the images of the local and visitor teams.
     *
     * @param localImageUrl The URL of the local team's image.
     * @param visitorImageUrl The URL of the visitor team's image.
     */
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
