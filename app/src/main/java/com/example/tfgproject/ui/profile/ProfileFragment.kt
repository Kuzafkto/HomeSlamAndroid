package com.example.tfgproject.ui.profile
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentProfileBinding
import com.example.tfgproject.model.User
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var toolbarViewModel: ToolbarViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userData.collect { user ->
                updateUI(user)
            }
        }
    }

    private fun updateUI(user: User?) {
        user?.let {
            if(it.nickname!=null){
                toolbarViewModel.setTitle(it.nickname!!)
            }else{
                toolbarViewModel.setTitle("????")
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
