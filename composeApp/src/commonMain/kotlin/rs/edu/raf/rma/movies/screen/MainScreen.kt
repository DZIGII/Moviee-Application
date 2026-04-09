package rs.edu.raf.rma.movies.screen

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.movies.coponents.MovieListItem
import rs.edu.raf.rma.movies.domain.Genre
import rs.edu.raf.rma.movies.domain.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val movies = listOf(
        Movie(
            id = "tt0111161",
            title = "The Shawshank Redemption",
            year = "1994",
            rating = 9.3,
            votes = 3171582,
            posterPath = "/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg",
            genres = listOf(
                Genre(1,"Drama"),
                Genre(2, "Crime")
            )
        ),
        Movie(
            id = "tt0068646",
            title = "The Godfather",
            year = "1972",
            rating = 9.2,
            votes = 2215919,
            posterPath = "/3bhkrj58Vtu7enYsRolD1fZdja1.jpg",
            genres = listOf(
                Genre(2,"Drama"),
                Genre(1,"Crime")
            )
        ),
        Movie(
            id = "tt0468569",
            title = "The Dark Knight",
            year = "2008",
            rating = 9.1,
            votes = 3150267,
            posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            genres = listOf(
                Genre(3,"Action"),
                Genre( 4,"Thriller"),
                Genre(1,"Crime")
            )
        ),
        Movie(
            id = "tt0050083",
            title = "12 Angry Men",
            year = "1957",
            rating = 9.0,
            votes = 978730,
            posterPath = "/2QXLVh32JKaWTjFJU3n8aIxRK9P.jpg",
            genres = listOf(
                Genre(2,"Drama")
            )
        ),
        Movie(
            id = "tt0071562",
            title = "The Godfather Part II",
            year = "1974",
            rating = 9.0,
            votes = 1488902,
            posterPath = "/hek3koDUyRQk7FIhPXsa6mT2Zc3.jpg",
            genres = listOf(
                Genre(1, "Drama"),
                Genre(2, "Crime")
            )
        )
    )

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopSection();
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(movies) { movie ->
                MovieListItem(movie)
            }
        }
    }

}


@Composable
fun TopSection() {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C2E))
            .padding(top = 50.dp, bottom = 10.dp, end = 20.dp, start = 20.dp)
    ) {

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🎬",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Premiere",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
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

            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF2A2A3E),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Sort: Rating ↓",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "1000 movies",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}