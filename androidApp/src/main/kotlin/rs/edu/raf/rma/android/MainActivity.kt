package rs.edu.raf.rma.android

import rs.edu.raf.rma.movies.screen.MovieScreen


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import rs.edu.raf.rma.movies.screen.MainScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MovieScreen()
//            MainScreen()
        }
    }
}