package com.example.tfgproject.ui.news

import android.R
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tfgproject.databinding.FragmentNoticiasBinding
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import com.google.firebase.auth.FirebaseAuth


class NoticiasFragment : Fragment() {

    private lateinit var toolbarViewModel: ToolbarViewModel
    private var _binding: FragmentNoticiasBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticiasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarViewModel.setTitle("Noticias")

        val userUID = auth.currentUser?.uid
        binding.userEmailTextView.text = userUID ?: "Usuario no autenticado"
    }
    override fun onResume() {
        super.onResume()
        toolbarViewModel.setTitle("Noticias")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

