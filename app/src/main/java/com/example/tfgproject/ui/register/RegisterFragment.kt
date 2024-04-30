package com.example.tfgproject.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.tfgproject.LoginActivity
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentRegisterBinding
import com.example.tfgproject.ui.login.LoginViewModel

class RegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = view.findViewById<EditText>(R.id.passwordEditText).text.toString()
            (activity as? LoginActivity)?.createUserWithEmailAndPassword(email, password)
        }

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            (activity as? LoginActivity)?.switchToLogin()
        }
    }
}
