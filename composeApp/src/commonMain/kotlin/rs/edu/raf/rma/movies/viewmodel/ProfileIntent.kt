package rs.edu.raf.rma.movies.viewmodel

sealed interface ProfileIntent {
    data object LoadProfile : ProfileIntent
    data object Logout : ProfileIntent
}
