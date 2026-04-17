package rs.edu.raf.rma.movies.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.viewmodel.koinViewModel
import rs.edu.raf.rma.movies.viewmodel.MoviesViewModel

@Composable
fun MoviesAppRoot() {
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