//package rs.edu.raf.rma.movies.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.toRoute
//import rs.edu.raf.rma.movies.screen.MainScreen
//import rs.edu.raf.rma.movies.screen.MovieScreen
//
//@Composable
//fun MovieNavigator() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = MainDestination
//    ) {
//        composable<MainDestination> {
//            MainScreen(
//                onMovieClick = { imdbId ->
//                    navController.navigate(MovieDetailsDestination(imdbId))
//                },
//                onFilterClick = {
//
//                }
//            )
//        }
//
//        composable<MovieDetailsDestination> { backStackEntry ->
//            val route = backStackEntry.toRoute<MovieDetailsDestination>()
//
//            MovieScreen(
//                imdbId = route.imdbId,
//                onBackClick = {
//                    navController.popBackStack()
//                }
//            )
//        }
//    }
//}