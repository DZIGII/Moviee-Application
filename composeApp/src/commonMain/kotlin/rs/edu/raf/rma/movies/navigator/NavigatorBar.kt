package rs.edu.raf.rma.movies.navigator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.compose.viewmodel.koinViewModel
import rs.edu.raf.rma.movies.screen.FavoritesScreen
import rs.edu.raf.rma.movies.screen.FilterScreen
import rs.edu.raf.rma.movies.screen.MainScreen
import rs.edu.raf.rma.movies.screen.MovieFilterUiState
import rs.edu.raf.rma.movies.screen.MovieScreen
import rs.edu.raf.rma.movies.screen.ProfileScreen
import rs.edu.raf.rma.movies.screen.QuizScreen
import rs.edu.raf.rma.movies.screen.WatchlistScreen
import rs.edu.raf.rma.movies.viewmodel.FavoritesIntent
import rs.edu.raf.rma.movies.viewmodel.FavoritesViewModel
import rs.edu.raf.rma.movies.viewmodel.MoviesViewModel
import rs.edu.raf.rma.movies.viewmodel.QuizIntent
import rs.edu.raf.rma.movies.viewmodel.QuizPhase
import rs.edu.raf.rma.movies.viewmodel.QuizViewModel

enum class BottomNavItem(val label: String, val icon: ImageVector) {
    MOVIES("Movies", Icons.Default.Movie),
    QUIZ("Quiz", Icons.Default.Quiz),
    FAVORITES("Favorites", Icons.Default.Star),
    WATCHLIST("Watchlist", Icons.Default.PlayArrow),
    PROFILE("Profile", Icons.Default.AccountCircle),
}

@Composable
fun MainNavigator() {
    var selectedTab by remember { mutableStateOf(BottomNavItem.MOVIES) }
    var pendingTab by remember { mutableStateOf<BottomNavItem?>(null) }
    val quizViewModel: QuizViewModel = koinViewModel()
    val quizState by quizViewModel.state.collectAsState()

    val isQuizActive = selectedTab == BottomNavItem.QUIZ &&
            (quizState.phase == QuizPhase.IN_PROGRESS || quizState.phase == QuizPhase.ANSWER_REVEALED)

    Scaffold(
        containerColor = Color(0xFF121212),
        bottomBar = {
            if (!isQuizActive) {
                NavigationBar(containerColor = Color(0xFF1C1C2E)) {
                    BottomNavItem.entries.forEach { item ->
                        NavigationBarItem(
                            selected = selectedTab == item,
                            onClick = { selectedTab = item },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF0A16),
                                selectedTextColor = Color(0xFFFF0A16),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF2A2A3E),
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                BottomNavItem.MOVIES -> MoviesTab()
                BottomNavItem.QUIZ -> QuizTab(
                    quizViewModel = quizViewModel,
                    onAbandonConfirmed = {
                        val target = pendingTab
                        pendingTab = null
                        if (target != null) {
                            selectedTab = target
                        }
                    },
                )
                BottomNavItem.PROFILE -> ProfileTab()
                BottomNavItem.FAVORITES -> FavoritesTab()
                BottomNavItem.WATCHLIST -> WatchListTab()
            }
        }
    }
}

@Composable
private fun MoviesTab() {
    val viewModel: MoviesViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    var showFilter by remember { mutableStateOf(false) }
    var selectedMovieId by remember { mutableStateOf<String?>(null) }
    var filters by remember { mutableStateOf(MovieFilterUiState()) }

    when {
        selectedMovieId != null -> {
            MovieScreen(
                imdbId = selectedMovieId!!,
                onBackClick = { selectedMovieId = null },

            )
        }

        showFilter -> {
            FilterScreen(
                filters = filters,
                genres = state.genres,
                onFiltersChange = { newFilters -> filters = newFilters },
                onBackClick = { showFilter = false },
                onApplyFilters = { newFilters ->
                    filters = newFilters
                    showFilter = false
                }
            )
        }

        else -> {
            MainScreen(
                onMovieClick = { imdbId -> selectedMovieId = imdbId },
                onFilterClick = { showFilter = true },
                activeFilters = filters
            )
        }
    }
}

@Composable
private fun QuizTab(
    quizViewModel: QuizViewModel,
    onAbandonConfirmed: () -> Unit,
) {
    QuizScreen(
        viewModel = quizViewModel,
        onAbandonConfirmed = onAbandonConfirmed,
    )
}

@Composable
private fun ProfileTab() {
    ProfileScreen()
}

@Composable
private fun FavoritesTab() {
    val viewModel: FavoritesViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    var selectedMovieId by remember { mutableStateOf<String?>(null)}

    when {
        selectedMovieId != null -> {
            MovieScreen(
                imdbId = selectedMovieId!!,
                onBackClick = { selectedMovieId = null },
            )
        }
        else -> {
            FavoritesScreen(
                onMovieClick = { imdbId -> selectedMovieId = imdbId }
            )
        }
    }
}

@Composable
private fun WatchListTab() {
    var selectedMovieId by remember { mutableStateOf<String?>(null) }

    when {
        selectedMovieId != null -> {
            MovieScreen(
                imdbId = selectedMovieId!!,
                onBackClick = { selectedMovieId = null },
            )
        }
        else -> {
            WatchlistScreen(
                onMovieClick = { imdbId -> selectedMovieId = imdbId }
            )
        }
    }
}
