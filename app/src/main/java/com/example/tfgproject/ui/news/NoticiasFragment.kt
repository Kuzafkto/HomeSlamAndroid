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


class NoticiasFragment : Fragment() {

    private lateinit var toolbarViewModel: ToolbarViewModel
    private var _binding: FragmentNoticiasBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener el ViewModel de la actividad para asegurar que se utiliza la misma instancia
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)
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

        // Establecer el título para la Toolbar cuando la vista del fragmento está lista
        toolbarViewModel.setTitle("Noticias")

        // Aquí podrías agregar más lógica de inicialización o configuración de la vista
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
