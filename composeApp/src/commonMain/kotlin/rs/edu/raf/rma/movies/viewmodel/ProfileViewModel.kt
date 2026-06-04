package rs.edu.raf.rma.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import rs.edu.raf.rma.core.auth.repository.AuthRepository
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.movies.data.remote.MovieApiService

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val api: MovieApiService,
    private val appDatabase: AppDatabase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeCounts()
        fetchProfile()
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> fetchProfile()
            is ProfileIntent.Logout -> logout()
        }
    }

    private fun observeCounts() {
        viewModelScope.launch {
            launch {
                appDatabase.movieDao().observeBestScore().collect { score ->
                    _state.value = _state.value.copy(bestScore = score)
                }
            }
            launch {
                appDatabase.movieDao().observeQuizCount().collect { count ->
                    _state.value = _state.value.copy(quizCount = count)
                }
            }
            launch {
                appDatabase.movieDao().observeFavoritesCount().collect { count ->
                    _state.value = _state.value.copy(favoritesCount = count)
                }
            }
            launch {
                appDatabase.movieDao().observeWatchlistCount().collect { count ->
                    _state.value = _state.value.copy(watchlistCount = count)
                }
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val user = api.getMe()
                _state.value = _state.value.copy(
                    loading = false,
                    fullName = user.fullName,
                    username = user.username,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                appDatabase.movieDao().deleteAllFavorites()
                appDatabase.movieDao().deleteAllWatchlist()
                appDatabase.movieDao().deleteAllQuizSessions()
                authRepository.logout()
                _effect.send(ProfileEffect.LoggedOut)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
