package com.team.common.components.textFields

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    label: Int,
    onTextChange: (String) -> Unit = {},
    isError: Boolean = false,
    errorMessage: String? = null,
    isEnabled: Boolean = true,
    isSingleLine: Boolean = true,
    maxLines: Int = 1,
) {
    OutlinedTextField(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        value = text,
        onValueChange = onTextChange,
        label = {
            Text(text = stringResource(id = label))
        },
        isError = isError,
        enabled = isEnabled,
        singleLine = isSingleLine,
        maxLines = maxLines,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(text = errorMessage)
            }
        }
    )
}

