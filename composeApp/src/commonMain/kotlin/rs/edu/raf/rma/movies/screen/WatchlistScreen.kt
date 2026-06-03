package rs.edu.raf.rma.movies.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import rs.edu.raf.rma.movies.coponents.GenreTag
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.viewmodel.MoviesEffect
import rs.edu.raf.rma.movies.viewmodel.WatchlistIntent
import rs.edu.raf.rma.movies.viewmodel.WatchlistViewModel

@Composable
fun WatchlistScreen(
    onMovieClick: (String) -> Unit,
) {

    val viewModel: WatchlistViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MoviesEffect.NavigateToDetails -> onMovieClick(effect.imdbId)
            }
        }
    }


    Scaffold(
        containerColor = Color(0xFF121212),
    ) { padding ->

        when {
            state.loading && state.movies.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null && state.movies.isEmpty() -> {
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
                        WatchlistMovieListItem(
                            movie = movie,
                            onClick = {
                                viewModel.onIntent(WatchlistIntent.OnMovieClicked(it.imdbId))
                            },
                            onRemove = {
                                viewModel.onIntent(WatchlistIntent.RemoveWatchlist(movie.imdbId))
                            }
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun WatchlistMovieListItem(
    movie: Movie,
    onClick: (Movie) -> Unit,
    onRemove: () -> Unit,
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(movie) }
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(11.dp, 8.dp)

        ) {

            AsyncImage(
                model = movie.posterPath?.let {
                    "https://image.tmdb.org/t/p/w500$it"
                },
                contentDescription = movie.title,
                modifier = Modifier
                    .width(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = movie.title,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = movie.year,
                    fontSize = 13.sp,
                    color = Color(0xFF999999)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "⭐ " + movie.imdbRating,
                        fontSize = 16.sp,
                        color = Color(0xFFF5C518)
                    )

                    Text(
                        text = movie.imdbVotes.toString() + " votes",
                        fontSize = 13.sp,
                        color = Color(0xFF777777)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    movie.genres.forEach { genre -> GenreTag(genre = genre) }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.BookmarkRemove,
                    contentDescription = "Remove from watchlist",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

    }

}
