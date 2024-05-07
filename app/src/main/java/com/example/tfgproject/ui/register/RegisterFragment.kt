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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.imageViewProfile.setImageURI(selectedImageUri)
            }
        }

        binding.buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val nickname = binding.nicknameEditText.text.toString()

            createUserWithEmailAndPassword(email, password, name, nickname)
        }

        binding.loginButton.setOnClickListener {
            (activity as? LoginActivity)?.switchToLogin()
        }
    }

    private fun createUserWithEmailAndPassword(email: String, password: String, name: String, nickname: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()||nickname.isEmpty()) {
            Toast.makeText(context, "Ninguno de los datos debe de estar vacio", Toast.LENGTH_SHORT).show()
            return
        }
        val activity = activity as? LoginActivity ?: return

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    val userData = hashMapOf(
                        "name" to name,
                        "nickname" to nickname,
                        "email" to email,
                        "picture" to "",
                        "userId" to it.uid,
                        "votes" to listOf<String>()
                    )

                    FirebaseFirestore.getInstance().collection("users").document(it.uid).set(userData)
                        .addOnSuccessListener {doc->
                            if (selectedImageUri != null) {
                                uploadImageToFirebaseStorage(selectedImageUri!!, it.uid)
                            } else {
                                activity.startActivity(Intent(activity, LoginActivity::class.java))
                                activity.finish()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error writing document", e)
                        }
                }
            } else {
                Log.e("Authentication", "Registration failed", task.exception)
                Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("users/$userId/profile.jpg")
        storageRef.putFile(imageUri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                FirebaseFirestore.getInstance().collection("users").document(userId).update("picture", imageUrl)
                    .addOnSuccessListener {
                        val activity = activity as? LoginActivity
                        activity?.startActivity(Intent(activity, LoginActivity::class.java))
                        activity?.finish()
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("Storage", "Upload failed", e)
        }
    }
}


