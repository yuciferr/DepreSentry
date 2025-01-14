package com.example.depresentry.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DSTextField(
    label: String,
    value: String,
    isPassword: Boolean = false,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = androidx.compose.ui.text.input.ImeAction.Done,
    onImeAction: () -> Unit = {},
    modifier: Modifier = Modifier,
    showKeyboard: Boolean = true,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFFCBC4CF)) },
        placeholder = { placeholder?.let { Text(it, color = Color(0xFFCBC4CF)) } },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() }
        ),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFF1E1622),
            focusedTextColor = Color(0xFFCBC4CF),
            unfocusedTextColor = Color(0xFFCBC4CF),
            cursorColor = Color(0xFFCBC4CF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLabelColor = Color(0xFFCBC4CF),
            unfocusedLabelColor = Color(0xFFCBC4CF),
            disabledTextColor = Color(0xFF8E8E8E),
            disabledLabelColor = Color(0xFF8E8E8E),
            disabledIndicatorColor = Color.Transparent
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .focusRequester(focusRequester)
            .clickable(enabled = enabled) {
                if (!showKeyboard) {
                    focusManager.clearFocus()
                }
            }
    )

}

@Composable
fun DSDropDown(
    label: String,
    value: String,
    items: List<String>,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable(enabled = enabled) { expanded = !expanded }) {

        DSTextField(
            label = label,
            value = value,
            placeholder = "Select $label",
            onValueChange = {},
            enabled = enabled,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable(enabled = enabled) { expanded = !expanded },
                    tint = if (enabled) Color(0xFFCBC4CF) else Color(0xFF8E8E8E)
                )
            },
            showKeyboard = false
        )

        if (enabled) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(item)
                            expanded = false
                        },
                        text = { Text(text = item) }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun DSTextFieldPreview() {
    var text by remember { mutableStateOf("") }
    DSTextField(
        label = "Email",
        value = text,
        onValueChange = { text = it }
    )
}