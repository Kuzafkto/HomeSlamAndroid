package com.example.tfgproject.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.tfgproject.LoginActivity
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentProfileBinding
import com.example.tfgproject.model.User
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Fragment to display and manage user profile.
 */
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var toolbarViewModel: ToolbarViewModel

    /**
     * Called to do initial creation of the fragment.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserData()
        setupLogoutButton()
    }

    /**
     * Sets up the logout button to sign out the user and navigate to the login screen.
     */
    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            navigateToLogin()
        }
    }

    /**
     * Navigates to the login screen.
     */
    private fun navigateToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    /**
     * Observes user data and updates the UI accordingly.
     */
    private fun observeUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userData.collect { user ->
                updateUI(user)
            }
        }
    }

    /**
     * Updates the UI with the user data.
     * @param user The user data to be displayed.
     */
    private fun updateUI(user: User?) {
        user?.let {
            if (it.nickname != null) {
                binding.profileTitle.text = it.nickname
            } else {
                toolbarViewModel.setTitle("????")
            }
            toolbarViewModel.setTitle(getString(R.string.profile))
            binding.emailTextView.text = it.email
            binding.nameTextView.text = it.name
            binding.nicknameTextView.text = it.nickname
            binding.profileImageView.load(it.picture) {
                crossfade(true)
                placeholder(R.drawable.profile_icon)
                error(R.drawable.error_image)
            }
        }
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the fragment.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
