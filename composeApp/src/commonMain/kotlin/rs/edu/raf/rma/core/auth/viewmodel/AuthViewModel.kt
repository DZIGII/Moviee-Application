package rs.edu.raf.rma.core.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import rs.edu.raf.rma.core.auth.repository.AuthRepository

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
)

sealed interface AuthIntent {
    data class Login(val username: String, val password: String) : AuthIntent
    data class Register(val fullName: String, val username: String, val password: String) : AuthIntent
}

sealed interface AuthEffect {
    data object NavigateToMain : AuthEffect
}

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Login -> login(intent.username, intent.password)
            is AuthIntent.Register -> register(intent.fullName, intent.username, intent.password)
        }
    }

    private fun login(username: String, password: String) {
        // Validacija
        if (username.length < 3) {
            _state.value = _state.value.copy(error = "Username must be at least 3 characters")
            return
        }
        if (password.length < 8) {
            _state.value = _state.value.copy(error = "Password must be at least 8 characters")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                authRepository.login(username, password)
                _state.value = _state.value.copy(loading = false, error = null)
                _effect.send(AuthEffect.NavigateToMain)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }

    private fun register(fullName: String, username: String, password: String) {
        // Validacija
        if (fullName.isBlank()) {
            _state.value = _state.value.copy(error = "Full name is required")
            return
        }
        if (username.length < 3) {
            _state.value = _state.value.copy(error = "Username must be at least 3 characters")
            return
        }
        val usernameRegex = Regex("^[a-zA-Z0-9_]+$")
        if (!usernameRegex.matches(username)) {
            _state.value = _state.value.copy(error = "Username can only contain letters, numbers and underscore")
            return
        }
        if (password.length < 8) {
            _state.value = _state.value.copy(error = "Password must be at least 8 characters")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                authRepository.register(fullName, username, password)
                _state.value = _state.value.copy(loading = false, error = null)
                _effect.send(AuthEffect.NavigateToMain)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Registration failed"
                )
            }
        }
    }
}
