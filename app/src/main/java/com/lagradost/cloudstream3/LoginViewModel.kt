package com.lagradost.cloudstream3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lagradost.cloudstream3.utils.NetworkResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val auth: FirebaseAuth
): ViewModel() {

    private val _login = MutableSharedFlow<NetworkResult<String>>()
    val login = _login.asSharedFlow()


    init {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                _login.emit(NetworkResult.Success("Login Success"))
            }
        }
    }

    fun login(email: String, password: String) {
        // Check for empty fields
        if (email.isEmpty() || password.isEmpty()) {
            viewModelScope.launch {
                _login.emit(NetworkResult.Error("Email or password cannot be empty"))
            }
            return
        }

        viewModelScope.launch {
            _login.emit(NetworkResult.Loading())
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _login.emit(NetworkResult.Success("Login Success"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(NetworkResult.Error(it.message.toString()))
                }
            }
    }
}