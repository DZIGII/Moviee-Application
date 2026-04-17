package rs.edu.raf.rma.movies.coponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import rs.edu.raf.rma.movies.domain.Movie
import rma_06_kotlin.composeapp.generated.resources.Res
import rma_06_kotlin.composeapp.generated.resources.movie
import coil3.compose.AsyncImage

@Composable
fun MovieListItem(
    movie: Movie,
    onClick: (Movie) -> Unit
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
                    .width(350.dp)
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
                    movie.genres.forEach { genre  -> GenreTag(genre = genre) }
                }



            }
        }

    }

}