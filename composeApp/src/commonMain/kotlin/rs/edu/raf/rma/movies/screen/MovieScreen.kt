package rs.edu.raf.rma.movies.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rma_06_kotlin.composeapp.generated.resources.Res
import rma_06_kotlin.composeapp.generated.resources.godfather
import rma_06_kotlin.composeapp.generated.resources.movie
import rs.edu.raf.rma.movies.coponents.CastItem
import rs.edu.raf.rma.movies.coponents.GenreTag
import rs.edu.raf.rma.movies.coponents.InfoBadge
import rs.edu.raf.rma.movies.viewmodel.MovieDetailsIntent
import rs.edu.raf.rma.movies.viewmodel.MovieDetailsViewModel

@Composable
fun MovieScreen(
    imdbId: String,
    onBackClick: () -> Unit,
) {
    val viewModel: MovieDetailsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(imdbId) {
        viewModel.onIntent(MovieDetailsIntent.LoadMovie(imdbId))
    }

    when {
        state.loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error ?: "Unknown error",
                    color = Color.White
                )
            }
        }

        state.movie == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No movie data",
                    color = Color.White
                )
            }
        }

        else -> {
            val movieDetail = state.movie!!

            Scaffold(
                containerColor = Color(0xFF121212)
            ) { padding ->

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {

                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500${movieDetail.backdropPath}",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.4f)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF121212)
                                        )
                                    )
                                )
                        )

                        Button(
                            onClick = {},
                            shape = RoundedCornerShape(1000.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text("▶", color = Color.White)
                        }

                        Button(
                            onClick = onBackClick,
                            modifier = Modifier.align(Alignment.TopStart),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Text(text = "<", color = Color.White)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-50).dp)
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w500${movieDetail.posterPath}",
                                contentDescription = null,
                                modifier = Modifier
                                    .width(120.dp)
                                    .clip(RoundedCornerShape(15.dp)),
                                contentScale = ContentScale.Crop,

                            )

                            Column {

                                Text(
                                    text = movieDetail.title,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "${movieDetail.year ?: ""} • ${movieDetail.runtime ?: ""} min",
                                    color = Color(0xFFCCCCCC),
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⭐ ${movieDetail.imdbRating ?: 0f} / 10",
                                    color = Color(0xFFF5C518),
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "${movieDetail.imdbVotes ?: 0} votes",
                                    color = Color(0xFF999999),
                                    fontSize = 16.sp
                                )
                            }

                            Text(
                                text = "TMDB ${movieDetail.tmdbRating ?: 0f}",
                                color = Color(0xFF4DABF7),
                                fontSize = 16.sp
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            movieDetail.genres.forEach { genre ->
                                GenreTag(genre)
                            }
                        }

                        IconButton(onClick = {
                            viewModel.onIntent(MovieDetailsIntent.ToggleFavorites(imdbId))
                        }) {
                            Icon(
                                imageVector = if (state.isFavorite)
                                    Icons.Default.Favorite else
                                    Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle favorite",
                                tint = if (state.isFavorite) Color(0xFFE91E63)
                                else Color.Gray,
                            )
                        }

                        IconButton(onClick = {
                            viewModel.onIntent(MovieDetailsIntent.ToggleWatchlist(imdbId))
                        }) {
                            Icon(
                                imageVector = if (state.isFavorite)
                                    Icons.Default.PlayArrow else
                                    Icons.Default.PlayArrow,
                                contentDescription = "Toggle favorite",
                                tint = if (state.isWatchlist) Color(0xFFE91E63)
                                else Color.Gray,
                            )
                        }

                        Text(
                            text = "OVERVIEW",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 20.dp)
                        )

                        Text(
                            text = movieDetail.overview ?: "",
                            color = Color(0xFF999999),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        Text(
                            text = "INFO",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 20.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 10.dp)
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    InfoBadge("Budget", "$${movieDetail.budget ?: 0}")
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    InfoBadge("Revenue", "$${movieDetail.revenue ?: 0}")
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    InfoBadge("Language", movieDetail.languageCode?.uppercase() ?: "")
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    InfoBadge("Popularity", movieDetail.popularity?.toString() ?: "")
                                }
                            }
                        }

                        Text(
                            text = "IMAGES",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 20.dp)
                        )

                        Row(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            state.images.forEach { image ->
                                AsyncImage(
                                    model = "https://image.tmdb.org/t/p/w500${image.filePath}",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(200.dp, 110.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Text(
                            text = "CAST",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 20.dp)
                        )

                        Column(
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            state.cast.forEach { castMember ->
                                CastItem(
                                    name = castMember.name,
                                    imagePath = castMember.profilePath
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}