package rs.edu.raf.rma.core.auth.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    val user: UserResponse,
)

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    @SerialName("full_name") val fullName: String,
)
