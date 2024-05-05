package com.example.tfgproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentProfileBinding
import com.example.tfgproject.ui.toolbar.ToolbarViewModel

class ProfileFragment : Fragment() {

    private lateinit var toolbarViewModel: ToolbarViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarViewModel.setTitle("Perfil")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
