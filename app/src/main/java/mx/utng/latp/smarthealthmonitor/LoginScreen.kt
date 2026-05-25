package mx.utng.smarthealthmonitor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import mx.utng.latp.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) {

    // TODO 1: Declarar estado para email, password, isLoading
    // Pista: var email by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }

    // TODO 2: Completar la función de validación
    fun validar(): Boolean {
        emailError = when {
            email.isBlank() -> "El email no puede estar vacío"
            !email.contains("@") -> "Email inválido"
            password.length < 6 -> "Mínimo 6 caracteres"
            else -> ""
        }
        return emailError.isEmpty()
    }

    SmartHealthMonitorTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // TODO 3: Ícono / Logo
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "SmartHealth Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "SmartHealth Monitor",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(32.dp))

                // TODO 4: Campo de email con isError
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    label = { Text("Correo electrónico") },
                    isError = emailError.isNotEmpty(),
                    supportingText = {
                        if (emailError.isNotEmpty())
                            Text(emailError, color = MaterialTheme.colorScheme.error)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // TODO 5: Campo de contraseña con toggle visibilidad
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = if (showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = "Toggle contraseña"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // TODO 6: Botón ENTRAR con estado loading
                Button(
                    onClick = {
                        if (validar()) {
                            isLoading = true
                            // Simular llamada a red
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("ENTRAR",
                            style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = {}) {
                    Text("¿Olvidaste tu contraseña?")
                }

            } // Column
        } // Scaffold
    } // Theme
}

