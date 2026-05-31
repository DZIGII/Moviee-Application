package rs.edu.raf.rma.core.auth.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("full_name") val fullName: String,
    val username: String,
    val password: String,
)
