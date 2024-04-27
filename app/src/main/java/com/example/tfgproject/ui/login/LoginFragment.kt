package com.example.tfgproject.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentLoginBinding
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var toolbarViewModel: ToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity()).get(ToolbarViewModel::class.java)

    }
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.isAuthenticated.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                findNavController().navigate(R.id.navigation_login)
            } else {
                // Mostrar error de inicio de sesión
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarViewModel.setTitle("Noticias")

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Inicia una coroutine para ejecutar la función suspendida
                //lifecycleScope.launch {
                    //val success = viewModel.login(email, password)
                    viewModel.login(this,email, password)
                    /*if (success) {
                        Log.d("LOGIN","LOGUEADOOOOOOO");
                        findNavController().navigate(R.id.navigation_noticias)
                    } else {
                        showToast("Error de autenticación")
                    }*/
                //}
            } else {
                showToast("Por favor, ingrese su correo electrónico y contraseña.")
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_register)
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    fun onLoginSuccess() {
        Log.d("ANDA?","SOOOOOOOOOOOOOO")
    }

    fun onLoginFailed(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
