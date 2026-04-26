package com.example.ciphrchat.ui_layer.auth.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ciphrchat.R
import com.example.ciphrchat.ui_layer.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSubmit: Button

    private val viewModel: RegisterFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etUsername = view.findViewById(R.id.registerFragment_editText_username)
        etPassword = view.findViewById(R.id.registerFragment_editText_password)
        btnSubmit = view.findViewById(R.id.registerFragment_button_submit)

        btnSubmit.setOnClickListener { onRegisterClicked() }
    }

    private fun onRegisterClicked() {
        if (!validateInput()) {
            showToast("Please fill in all fields")
            return
        }
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { viewModel.register(username, password) }
            when (result) {
                RegisterFragmentViewModel.RegisterResult.SUCCESS -> startMainActivity()
                RegisterFragmentViewModel.RegisterResult.USERNAME_TAKEN -> showToast("Username already taken")
                RegisterFragmentViewModel.RegisterResult.SERVER_ERROR -> showToast("Could not reach server")
                RegisterFragmentViewModel.RegisterResult.LOCAL_ERROR -> showToast("Something went wrong")
            }
        }
    }

    private fun validateInput(): Boolean {
        return etUsername.text.toString().trim().isNotEmpty() && etPassword.text.toString().trim()
            .isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun startMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }
}