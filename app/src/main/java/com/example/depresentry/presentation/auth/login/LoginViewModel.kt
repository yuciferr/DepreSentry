package com.example.depresentry.presentation.auth.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.model.UserCredentials
import com.example.depresentry.domain.usecase.auth.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    // State variables to track loading, error, and success
    var isLoading = mutableStateOf(false)
    var loginError = mutableStateOf<String?>(null)
    var loginSuccess = mutableStateOf(false)

    fun loginUser(email: String, password: String) {
        val userCredentials = UserCredentials(email = email, password = password, fullName = null)

        viewModelScope.launch {
            isLoading.value = true
            loginError.value = null

            val result = loginUserUseCase(userCredentials)
            result.onSuccess {
                loginSuccess.value = it
            }.onFailure { exception ->
                loginError.value = exception.message
            }

            isLoading.value = false
        }
    }
}
