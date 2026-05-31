package rs.edu.raf.rma.core.auth.repository

import rs.edu.raf.rma.core.auth.AuthStore
import rs.edu.raf.rma.core.auth.api.AuthApiService
import rs.edu.raf.rma.core.auth.api.LoginRequest
import rs.edu.raf.rma.core.auth.api.RegisterRequest
import rs.edu.raf.rma.core.auth.model.AuthData

class AuthRepositoryImpl(
    private val authApi: AuthApiService,
    private val authStore: AuthStore,
) : AuthRepository {

    override suspend fun login(username: String, password: String) {
        val response = authApi.login(
            LoginRequest(username = username, password = password)
        )
        authStore.setAuthData(
            AuthData(accessToken = response.accessToken)
        )
    }

    override suspend fun register(fullName: String, username: String, password: String) {
        val response = authApi.register(
            RegisterRequest(fullName = fullName, username = username, password = password)
        )
        authStore.setAuthData(
            AuthData(accessToken = response.accessToken)
        )
    }

    override suspend fun logout() {
        authStore.clearAuthData()
    }
}
