package com.example.ciphrchat.ui_layer.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.repositories.UserRepository
import com.example.ciphrchat.ui_layer.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    private lateinit var tvUsername: TextView
    private lateinit var etPassword: EditText
    private lateinit var btnSubmit: Button

    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUsername = view.findViewById(R.id.loginFragment_textView_username)
        etPassword = view.findViewById(R.id.loginFragment_editText_password)
        btnSubmit = view.findViewById(R.id.loginFragment_button_submit)

        lifecycleScope.launch {
            val username = withContext(Dispatchers.IO) { viewModel.getUsername() }
            tvUsername.text = username
        }

        btnSubmit.setOnClickListener { onLoginClicked() }
    }

    private fun onLoginClicked() {
        if (!validateInput()) {
            showToast("Please enter password")
            return
        }
        val username = tvUsername.text.toString()
        val password = etPassword.text.toString()
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { viewModel.login(username, password) }
            when (result) {
                LoginFragmentViewModel.AuthResult.SUCCESS -> startMainActivity()
                LoginFragmentViewModel.AuthResult.INVALID_CREDENTIALS -> showToast("Invalid credentials")
                LoginFragmentViewModel.AuthResult.SERVER_ERROR -> showToast("Could not reach server")
                LoginFragmentViewModel.AuthResult.LOCAL_ERROR -> showToast("Something went wrong")
            }
        }
    }

    private fun validateInput(): Boolean {
        return etPassword.text.toString().trim().isNotEmpty() && tvUsername.text.toString().trim().isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun startMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

}