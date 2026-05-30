package rs.edu.raf.rma.movies.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginClick: (username: String, password: String) -> Unit,
    onRegisterClick: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    val backgroundColor = Color(0xFF05070D)
    val fieldColor = Color(0xFF272847)
    val accentRed = Color(0xFFFF0A16)
    val textWhite = Color(0xFFF5F5F5)
    val hintColor = Color(0xFF8E90A6)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Showtime",
                color = accentRed,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to your account",
                color = hintColor,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            UsernameInputField(
                value = username,
                onValueChange = { username = it },
                fieldColor = fieldColor,
                hintColor = hintColor,
                textColor = textWhite
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordInputField(
                value = password,
                onValueChange = { password = it },
                fieldColor = fieldColor,
                hintColor = hintColor,
                textColor = textWhite
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = accentRed,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onLoginClick(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentRed
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.height(24.dp)
                    )
                } else {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = fieldColor
                ),
                enabled = !isLoading
            ) {
                Text(
                    text = "Create Account",
                    color = textWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UsernameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    fieldColor: Color,
    hintColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(fieldColor)
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = "Username",
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
                fontSize = 16.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    fieldColor: Color,
    hintColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(fieldColor)
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = "Password",
                color = hintColor,
                fontSize = 15.sp
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
