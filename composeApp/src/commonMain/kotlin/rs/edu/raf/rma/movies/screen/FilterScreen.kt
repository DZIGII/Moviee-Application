package rs.edu.raf.rma.movies.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.movies.domain.Genre

data class MovieFilterUiState(
    val searchQuery: String = "",
    val selectedGenre: String? = null,
    val fromYear: String = "1923",
    val toYear: String = "2025",
    val minRating: Float = 10f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    filters: MovieFilterUiState,
    genres: List<Genre>,
    onFiltersChange: (MovieFilterUiState) -> Unit,
    onBackClick: () -> Unit,
    onApplyFilters: (MovieFilterUiState) -> Unit
) {

    val backgroundColor = Color(0xFF05070D)
    val topBarColor = Color(0xFF151733)
    val fieldColor = Color(0xFF272847)
    val chipColor = Color(0xFF2A2C4E)
    val accentRed = Color(0xFFFF0A16)
    val textWhite = Color(0xFFF5F5F5)
    val hintColor = Color(0xFF8E90A6)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarColor)
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Filter Movies",
                    color = textWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Clear All",
                    color = accentRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        onFiltersChange(MovieFilterUiState())
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 20.dp)
            ) {
                SectionTitle("SEARCH")

                RoundedInputField(
                    value = filters.searchQuery,
                    onValueChange = {
                        onFiltersChange(filters.copy(searchQuery = it))
                    },
                    hint = "Search by movie title...",
                    fieldColor = fieldColor,
                    hintColor = hintColor,
                    textColor = textWhite
                )

                Spacer(modifier = Modifier.height(26.dp))

                SectionTitle("GENRE")

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    genres.forEach { genre ->
                        val selected = filters.selectedGenre == genre.name

                        GenreChip(
                            text = genre.name,
                            selected = selected,
                            onClick = {
                                onFiltersChange(
                                    filters.copy(
                                        selectedGenre = if (selected) null else genre.name
                                    )
                                )
                            },
                            selectedColor = accentRed,
                            unselectedColor = chipColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle("YEAR RANGE")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        SmallLabel("From")
                        Spacer(modifier = Modifier.height(8.dp))
                        RoundedInputField(
                            value = filters.fromYear,
                            onValueChange = {
                                onFiltersChange(
                                    filters.copy(
                                        fromYear = it.filter { ch -> ch.isDigit() }.take(4)
                                    )
                                )
                            },
                            hint = "",
                            fieldColor = fieldColor,
                            hintColor = hintColor,
                            textColor = textWhite,
                            centerText = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text(
                        text = "—",
                        color = Color.White,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        SmallLabel("To")
                        Spacer(modifier = Modifier.height(8.dp))
                        RoundedInputField(
                            value = filters.toYear,
                            onValueChange = {
                                onFiltersChange(
                                    filters.copy(
                                        toYear = it.filter { ch -> ch.isDigit() }.take(4)
                                    )
                                )
                            },
                            hint = "",
                            fieldColor = fieldColor,
                            hintColor = hintColor,
                            textColor = textWhite,
                            centerText = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle("MINIMUM RATING")

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = filters.minRating,
                        onValueChange = {
                            onFiltersChange(filters.copy(minRating = it))
                        },
                        valueRange = 0f..10f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = accentRed,
                            activeTrackColor = accentRed,
                            inactiveTrackColor = Color(0xFF41202A)
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(fieldColor)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = ((filters.minRating * 10).toInt() / 10.0).toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(34.dp))

                Button(
                    onClick = { onApplyFilters(filters) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentRed
                    )
                ) {
                    Text(
                        text = "Apply Filters",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color(0xFFFFF2D8),
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun SmallLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFFB7B7C9),
        fontSize = 11.sp
    )
}

@Composable
private fun GenreChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    unselectedColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) selectedColor else unselectedColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RoundedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    fieldColor: Color,
    hintColor: Color,
    textColor: Color,
    centerText: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(fieldColor)
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        if (value.isEmpty() && hint.isNotEmpty()) {
            Text(
                text = hint,
                color = hintColor,
                fontSize = 15.sp
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontWeight = if (centerText) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (centerText) Alignment.Center else Alignment.CenterStart
                ) {
                    innerTextField()
                }
            }
        )
    }
}