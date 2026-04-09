package rs.edu.raf.rma.movies.coponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import rs.edu.raf.rma.movies.domain.Movie
import rma_06_kotlin.composeapp.generated.resources.Res
import rma_06_kotlin.composeapp.generated.resources.movie

@Composable
fun MovieListItem(movie: Movie) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(11.dp, 8.dp)

        ) {

            Image(
                painter = painterResource(Res.drawable.movie),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp, 100.dp)
                    .clip(RoundedCornerShape(15.dp))
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
                        text = "⭐ " + movie.rating,
                        fontSize = 16.sp,
                        color = Color(0xFFF5C518)
                    )

                    Text(
                        text = movie.votes.toString() + " votes",
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