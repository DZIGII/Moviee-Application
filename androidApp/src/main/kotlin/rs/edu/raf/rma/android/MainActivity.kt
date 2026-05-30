package rs.edu.raf.rma.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import rs.edu.raf.rma.movies.screen.LoginScreen
import rs.edu.raf.rma.movies.screen.MoviesAppRoot
import rs.edu.raf.rma.movies.screen.RegistrationScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

//            LoginScreen(
//                onLoginClick = { username, password -> },
//                onRegisterClick = { },
//                isLoading = false,
//                error = null
//            )

//            RegistrationScreen(
//                onRegisterClick = { fullName, username, password -> },
//                onLoginClick = { },
//                isLoading = false,
//                error = null
//            )

            MoviesAppRoot()
        }
    }
}