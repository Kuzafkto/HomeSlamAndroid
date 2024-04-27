package com.example.tfgproject.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentPartidosBinding // Asegúrate de tener este import correcto
import com.example.tfgproject.ui.toolbar.ToolbarViewModel

class PartidosFragment : Fragment() {

    private lateinit var toolbarViewModel: ToolbarViewModel
    private var _binding: FragmentPartidosBinding? = null
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
        _binding = FragmentPartidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Establecer el título para la Toolbar cuando la vista del fragmento está lista
        toolbarViewModel.setTitle("Partidos")

        // Aquí puedes añadir más lógica para inicializar la vista o configurar observadores, etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpiar la referencia al binding para evitar fugas de memoria
    }
}
