package rs.edu.raf.rma.movies.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import rs.edu.raf.rma.movies.coponents.MovieListItem
import rs.edu.raf.rma.movies.domain.SortOption
import rs.edu.raf.rma.movies.viewmodel.MoviesIntent
import rs.edu.raf.rma.movies.viewmodel.MoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onMovieClick: (String) -> Unit,
    onFilterClick: () -> Unit,
    activeFilters: MovieFilterUiState
) {
    val viewModel: MoviesViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(activeFilters) {
        val hasActiveFilters =
            activeFilters.searchQuery.isNotBlank() ||
                    activeFilters.selectedGenre != null ||
                    activeFilters.fromYear != "1923" ||
                    activeFilters.toYear != "2025" ||
                    activeFilters.minRating != 10f

        if (hasActiveFilters) {
            viewModel.applyFilters(activeFilters)
        } else {
            viewModel.loadMovies()
        }
    }

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopSection(
                movieCount = state.movies.size,
                sortBy = state.sortBy,
                onFilterClick = onFilterClick,
                onSortChange = { viewModel.onIntent(MoviesIntent.ChangeSortBy(it)) }
            )
        }
    ) { padding ->

        when {
            state.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Error",
                        color = Color.White
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(state.movies) { movie ->
                        MovieListItem(
                            movie = movie,
                            onClick = {
                                onMovieClick(it.imdbId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopSection(
    movieCount: Int,
    sortBy: SortOption,
    onFilterClick: () -> Unit,
    onSortChange: (SortOption) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C2E))
            .padding(top = 50.dp, bottom = 10.dp, end = 20.dp, start = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎬", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Premiere", color = Color.White, fontSize = 18.sp)
            }

            Button(
                onClick = onFilterClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Filter")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF2A2A3E), shape = RoundedCornerShape(20.dp))
                        .clickable { dropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Sort: ${sortBy.label} ↓",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    containerColor = Color(0xFF2A2A3E)
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label, color = if (option == sortBy) Color.Red else Color.White) },
                            onClick = {
                                onSortChange(option)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Text(text = "$movieCount movies", color = Color.Gray, fontSize = 12.sp)
        }
    }
}