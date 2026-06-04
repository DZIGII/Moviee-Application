package rs.edu.raf.rma.movies.viewmodel

sealed interface ProfileEffect {
    data object LoggedOut : ProfileEffect
}
