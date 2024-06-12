package com.example.tfgproject.ui.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.tfgproject.LoginActivity
import com.example.tfgproject.R
import com.example.tfgproject.databinding.FragmentRegisterBinding
import com.example.tfgproject.CameraActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Fragment for user registration. This fragment allows users to register with email and password,
 * select or take a profile picture, and save their data to Firebase.
 */
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the launcher for selecting an image from the gallery
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.imageViewProfile.setImageURI(selectedImageUri)
            }
        }

        // Initialize the launcher for taking a photo
        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoUriString = result.data?.getStringExtra("photo_uri")
                selectedImageUri = photoUriString?.toUri()
                binding.imageViewProfile.setImageURI(selectedImageUri)
            }
        }

        binding.buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }

        binding.buttonTakePhoto.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            takePhotoLauncher.launch(intent)
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

    /**
     * Creates a user with the provided email and password, and stores additional user data in Firestore.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param name The user's full name.
     * @param nickname The user's nickname.
     */
    private fun createUserWithEmailAndPassword(email: String, password: String, name: String, nickname: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || nickname.isEmpty()) {
            Snackbar.make(binding.root, getString(R.string.non_empty_field_error), Snackbar.LENGTH_LONG).show()
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
                        "votes" to listOf<String>(),
                        "isAdmin" to false,
                        "isOwner" to false
                    )

                    FirebaseFirestore.getInstance().collection("users").document(it.uid).set(userData)
                        .addOnSuccessListener { doc ->
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
                Snackbar.make(binding.root, getString(R.string.registration_failed), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Uploads the selected image to Firebase Storage and updates the user's profile picture URL in Firestore.
     *
     * @param imageUri The URI of the selected image.
     * @param userId The user's ID.
     */
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
