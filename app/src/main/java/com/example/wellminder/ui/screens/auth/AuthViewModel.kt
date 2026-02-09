package com.example.wellminder.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellminder.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Temporary storage for modification flow
    var tempName: String = ""
    var tempGender: String = "Male"
    var tempWeight: Float = 60f
    var tempHeight: Int = 170
    var tempBirthDate: Long = 0L

    fun register(email: String, passwordHash: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val success = authRepository.registerUser(
                name = tempName,
                email = email,
                passwordHash = passwordHash,
                gender = tempGender,
                height = tempHeight,
                weight = tempWeight,
                birthDate = tempBirthDate
            )
            if (success) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Registration Failed")
            }
        }
    }

    fun login(email: String, passwordHash: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = authRepository.loginUser(email, passwordHash)
            if (user != null) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Invalid Credentials")
            }
        }
    }

    fun continueGuest() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val success = authRepository.continueAsGuest(
                name = tempName, // Guests enter name too? Or default? Assuming name input.
                gender = tempGender,
                height = tempHeight,
                weight = tempWeight,
                birthDate = tempBirthDate
            )
             if (success) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Guest Login Failed")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
