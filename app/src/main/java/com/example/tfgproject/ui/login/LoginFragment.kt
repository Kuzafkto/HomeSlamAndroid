package com.example.tfgproject.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tfgproject.LoginActivity
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentLoginBinding
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = view.findViewById<EditText>(R.id.passwordEditText).text.toString()
            (activity as? LoginActivity)?.signInWithEmailAndPassword(email, password)
        }

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            (activity as? LoginActivity)?.switchToRegister()
        }
    }
}

