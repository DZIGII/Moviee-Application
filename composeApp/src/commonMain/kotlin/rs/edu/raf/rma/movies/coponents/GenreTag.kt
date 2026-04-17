package rs.edu.raf.rma.movies.coponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.movies.domain.Genre

@Composable
fun GenreTag(
    genre: Genre
) {
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFF333333),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 11.dp, vertical = 5.dp)
    ) {
        Text(
            text = genre.name,
            fontSize = 13.sp,
            color = Color(0xFFCCCCCC)
        )
    }
}