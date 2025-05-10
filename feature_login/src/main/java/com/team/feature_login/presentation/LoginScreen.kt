package com.team.feature_login.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.common.components.buttons.CustomButton
import com.team.common.components.textFields.CustomTextField
import com.team.common.components.warning.WarningWindow
import com.team.feature_login.R
import com.team.feature_login.data.model.TokenResult
import com.team.feature_login.presentation.state.LoginState

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state

    if (state.isSuccess) {
        onLoginSuccess()
        return
    }

    LoginScreenContent(
        state = state,
        onUsernameChange = { viewModel.onEvent(LoginEvent.OnUsernameChange(it)) },
        onPasswordChange = { viewModel.onEvent(LoginEvent.OnPasswordChange(it)) },
        onLoginClick = { viewModel.onEvent(LoginEvent.OnLoginClick) },
        onWarningDismiss = { viewModel.onEvent(LoginEvent.OnWarningClose) }
    )
}

@Composable
private fun LoginScreenContent(
    state: LoginState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onWarningDismiss: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    var passwordVisible by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(35, 164, 255, 255).copy(alpha = 0.6f))
            .imePadding()
            .padding(16.dp)
    ) {
        AnimatedVisibility(visible = state.error != null) {
            state.error?.let {
                WarningWindow(
                    title = "Что-то пошло не так",
                    description = it,
                    onDismiss = onWarningDismiss
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginCard(
                username = state.username,
                password = state.password,
                isLoading = state.isLoading,
                passwordVisible = passwordVisible,
                usernameError = usernameError,
                passwordError = passwordError,
                onUsernameChange = { 
                    onUsernameChange(it)
                    usernameError = if (it.isBlank()) context.getString(R.string.enter_login) else null
                },
                onPasswordChange = {
                    onPasswordChange(it)
                    passwordError = if (it.isBlank()) context.getString(R.string.enter_password) else null
                },
                onPasswordVisibilityChange = { passwordVisible = it },
                onLoginClick = {
                    keyboardController?.hide()
                    onLoginClick()
                }
            )
        }
    }
}

@Composable
private fun LoginCard(
    username: String,
    password: String,
    isLoading: Boolean,
    passwordVisible: Boolean,
    usernameError: String?,
    passwordError: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoimage),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = username,
                onValueChange = onUsernameChange,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.person_24px),
                        contentDescription = null
                    )
                },
                label = stringResource(R.string.username_label),
                error = usernameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            CustomTextField(
                value = password,
                onValueChange = onPasswordChange,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.lock_24px),
                        contentDescription = null
                    )
                },
                label = stringResource(R.string.password_label),
                error = passwordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) R.drawable.visibility_24px else R.drawable.visibility_off_24px

                    IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(image),
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = stringResource(R.string.login_button),
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                enabled = usernameError == null && passwordError == null && 
                         username.isNotBlank() && password.isNotBlank()
            )
        }
    }
}
