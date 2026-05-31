package rs.edu.raf.rma.movies.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import rs.edu.raf.rma.core.auth.AuthStore
import rs.edu.raf.rma.core.auth.model.AuthState
import rs.edu.raf.rma.core.auth.viewmodel.AuthIntent
import rs.edu.raf.rma.core.auth.viewmodel.AuthViewModel
import rs.edu.raf.rma.movies.viewmodel.MoviesViewModel

@Composable
fun MoviesAppRoot() {
    val authStore: AuthStore = koinInject()
    val authState by authStore.authState.collectAsState()

    when (authState) {
        is AuthState.Unauthenticated -> {
            AuthFlow()
        }
        is AuthState.Authenticated -> {
            MainFlow()
        }
    }
}

@Composable
private fun AuthFlow() {
    val authViewModel: AuthViewModel = koinViewModel()
    val state by authViewModel.state.collectAsState()

    var showRegister by remember { mutableStateOf(false) }


    if (showRegister) {
        RegistrationScreen(
            onRegisterClick = { fullName, username, password ->
                authViewModel.onIntent(AuthIntent.Register(fullName, username, password))
            },
            onLoginClick = { showRegister = false },
            isLoading = state.loading,
            error = state.error
        )
    } else {
        LoginScreen(
            onLoginClick = { username, password ->
                authViewModel.onIntent(AuthIntent.Login(username, password))
            },
            onRegisterClick = { showRegister = true },
            isLoading = state.loading,
            error = state.error
        )
    }
}

@Composable
private fun MainFlow() {
    val viewModel: MoviesViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    var showFilter by remember { mutableStateOf(false) }
    var selectedMovieId by remember { mutableStateOf<String?>(null) }
    var filters by remember { mutableStateOf(MovieFilterUiState()) }

    when {
        selectedMovieId != null -> {
            MovieScreen(
                imdbId = selectedMovieId!!,
                onBackClick = {
                    selectedMovieId = null
                }
            )
        }

        showFilter -> {
            FilterScreen(
                filters = filters,
                genres = state.genres,
                onFiltersChange = { newFilters ->
                    filters = newFilters
                },
                onBackClick = {
                    showFilter = false
                },
                onApplyFilters = { newFilters ->
                    filters = newFilters
                    showFilter = false
                }
            )
        }

        else -> {
            MainScreen(
                onMovieClick = { imdbId ->
                    selectedMovieId = imdbId
                },
                onFilterClick = {
                    showFilter = true
                },
                activeFilters = filters
            )
        }
    }
}
