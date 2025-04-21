package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun LogResultDialog(
    onDismiss: () -> Unit,
    onConfirm: (iterations: Int, weight: Float?, seconds: Int?) -> Unit,
) {
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var secs by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 16.dp,
        title = { Text("Запис результату") },
        text = {
            Column {
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Повторення") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Вага (кг)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = secs,
                    onValueChange = { secs = it },
                    label = { Text("Секунди") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // при натисканні Done на клавіатурі також підтверджуємо
                            val i = reps.toIntOrNull() ?: 0
                            val w = weight.toFloatOrNull()
                            val s = secs.toIntOrNull()
                            onConfirm(i, w, s)
                        }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val i = reps.toIntOrNull() ?: 0
                val w = weight.toFloatOrNull()
                val s = secs.toIntOrNull()
                onConfirm(i, w, s)
            }) {
                Text("ОК")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}
